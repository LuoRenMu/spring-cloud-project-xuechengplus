import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author LoMu
 * Date  2023-05-17 9:21
 */


@Slf4j
public class TestMinIo {
    private final MinioClient minioClient = MinioClient.builder()
            //minio连接
            .endpoint("http://192.168.1.103:9000/")
            //密码
            .credentials("minioadmin", "minioadmin")
            .build();

    @Test
    void testUploadFile() throws Exception {
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                //确定桶
                .bucket("video")
                //文件路径
                .filename("C:\\Users\\28427\\Desktop\\imgs\\0230517102208.jpg")
                //对象名
                .object("c/d/cdd49827732a2b61974aec0570b49e8d/chunk/0")
                .build();

        minioClient.uploadObject(testbucket);
    }

    @Test
    void testDeleteFile() throws Exception {
        RemoveObjectArgs testbucket = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("0230517102208.jpg")
                .build();
        minioClient.removeObject(testbucket);
    }

    @Test
    void testGetFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs testbucket = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("0230517102208.jpg")
                .build();
        FilterInputStream filterInputStream = minioClient.getObject(testbucket);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\28427\\Desktop\\1.jpg");
        byte[] b = new byte[1024];
        int r;
        while ((r = filterInputStream.read(b)) != -1) {
            fileOutputStream.write(b, 0, r);
            fileOutputStream.flush();
        }

        filterInputStream.close();
        fileOutputStream.close();
    }

    @Test
    void testMinIOMerge() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //MinIo限制分块文件必须为5MB
        List<ComposeSource> testbucket = Stream.iterate(0, i -> ++i)
                .limit(2)
                .map(i ->
                        ComposeSource.builder()
                                .bucket("testbucket")
                                .object("chunk/" + i)
                                .build()
                ).collect(Collectors.toList());
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge/1.png")
                .sources(testbucket).build();

        minioClient.composeObject(composeObjectArgs);
    }

    @Test
    void uploadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File("C:\\Users\\28427\\Desktop\\imgs\\chunk\\");
        File[] listFiles = file.listFiles();
        for (int i = 0; i < Objects.requireNonNull(listFiles).length; i++) {
            UploadObjectArgs build = UploadObjectArgs.builder().bucket("testbucket")
                    .object("chunk\\" + i)
                    .filename(file.toPath() + "\\" + i)
                    .build();
            minioClient.uploadObject(build);
        }
    }

    @Test
    void verifyMD5Hex() {
        GetObjectArgs testbucket = GetObjectArgs
                .builder()
                .bucket("testbucket")
                .object("02305117102208.jpg")
                .build();


        try {
            GetObjectResponse object = minioClient.getObject(testbucket);
            String md5Hex = DigestUtils.md5Hex(object);
            System.out.println(md5Hex);
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\28427\\Desktop\\imgs\\0230517102208.jpg");
            System.out.println(DigestUtils.md5Hex(fileInputStream));
            fileInputStream.close();
        } catch (ErrorResponseException e) {
            System.out.println("6666");
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void putStreamFile() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream inputStream = new FileInputStream("D:\\images\\99719255.jpg");
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("testbucket")
                .object("img/1.png")
                .stream(inputStream, inputStream.available(), -1)
                .build();
        minioClient.putObject(args);
        inputStream.close();
    }

}

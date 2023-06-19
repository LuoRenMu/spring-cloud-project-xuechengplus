import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author LoMu
 * Date  2023-05-21 8:34
 */

public class TestFileChunkMerge {
    public static void main(String[] args) throws IOException {


    }

    private static void merge() {
        File fileDir = new File("C:\\Users\\28427\\Desktop\\imgs\\chunk");
        File[] files = fileDir.listFiles();
        try {
            RandomAccessFile w = new RandomAccessFile("C:\\Users\\28427\\Desktop\\imgs\\chunk\\123.png", "rw");
            assert files != null;
            List<File> list = Arrays.asList(files);
            list.sort(Comparator.comparingInt(a -> Integer.parseInt(a.getName())));
            for (File file : list) {
                RandomAccessFile r = new RandomAccessFile(file, "r");
                int len;
                byte[] bytes = new byte[1024];
                while ((len = r.read(bytes)) != -1) {
                    w.write(bytes, 0, len);
                }
                r.close();
            }
            w.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void chunk() {
        int chunkSize = 1024 * 1024 * 5;
        File fileinput = new File("C:\\Users\\28427\\Desktop\\imgs\\96796803_p1.jpg");
        try (InputStream inputStream = Files.newInputStream(fileinput.toPath())) {
            int chunkNum = (int) Math.ceil((double) fileinput.length() / chunkSize);
            int len;
            byte[] b = new byte[chunkSize];
            for (int i = 0; i < chunkNum; i++) {
                File file = new File("C:\\Users\\28427\\Desktop\\imgs\\chunk\\" + i);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                if ((len = inputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, len);
                }
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

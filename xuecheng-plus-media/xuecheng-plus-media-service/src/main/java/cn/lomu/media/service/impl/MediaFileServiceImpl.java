package cn.lomu.media.service.impl;

import cn.lomu.base.exception.XueChengPlusException;
import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.media.mapper.MediaProcessMapper;
import cn.lomu.media.model.dto.QueryMediaParamsDto;
import cn.lomu.media.model.dto.UploadFileParamsDTO;

import cn.lomu.media.model.po.MediaFiles;
import cn.lomu.media.model.po.MediaProcess;
import cn.lomu.media.service.MediaFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.lomu.media.mapper.MediaFilesMapper;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.*;
import java.nio.file.Files;


import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@RefreshScope
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket.files}")
    String bucketFiles;
    @Value("${minio.bucket.videofiles}")
    String bucketVideos;

    @Autowired
    MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

    }


    private void putMinIoBucket(String bucket, InputStream inputStream, String hexFileName, String originalFileName) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(hexFileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(getMimeType(getFileType(originalFileName)))
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void saveMediaProcess(MediaFiles mediaFiles) {
        MediaProcess mediaProcess = new MediaProcess();
        BeanUtils.copyProperties(mediaFiles, mediaProcess);
        mediaProcess.setUrl(null);
        mediaProcess.setCreateDate(LocalDateTime.now());
        mediaProcess.setFailCount(0);
        mediaProcess.setStatus("1");
        mediaProcessMapper.insert(mediaProcess);
    }

    private String getDateFilePath(String fileName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("/yyyy/MM/dd/");
        String format = LocalDateTime.now().format(dtf);
        return format + fileName;
    }


    @Override
    public MediaFiles uploadFile(Long companyId, UploadFileParamsDTO uploadFileParamsDTO, String objectName, InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while (true) {
            try {
                if ((len = inputStream.read(buffer)) == -1) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            outputStream.write(buffer, 0, len);
        }
        InputStream arrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        String md5Hex = getmd5Hex(arrayInputStream);
        MediaFiles mediaFileById = getMediaFileById(md5Hex);
        if (mediaFileById != null) {
            return mediaFileById;
        }
        String hexFileName = md5Hex + getFileType(uploadFileParamsDTO.getFileName());
        if (StringUtils.isBlank(objectName)) {
            objectName = getDateFilePath(hexFileName);
        } else {
            uploadFileParamsDTO.setCurrentObjectName("/" + bucketFiles + "/" + objectName);
        }
        arrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        putMinIoBucket(bucketFiles, arrayInputStream, objectName, uploadFileParamsDTO.getFileName());
        currentProxy.saveMediaDateBase(companyId, uploadFileParamsDTO, md5Hex, hexFileName);
        return getMediaFileById(md5Hex);
    }

    @NotNull
    private static String getFileType(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex);
    }

    @Override
    @Transactional
    public void saveMediaDateBase(Long companyId, UploadFileParamsDTO uploadFileParamsDTO, String hex, String fileName) {
        MediaFiles mediaFiles = new MediaFiles();
        BeanUtils.copyProperties(uploadFileParamsDTO, mediaFiles);
        String filePath = fileName;

        mediaFiles.setId(hex);
        mediaFiles.setFileId(hex);
        mediaFiles.setCompanyId(companyId);
        if (uploadFileParamsDTO.isVideo()) {
            mediaFiles.setBucket(bucketVideos);
            mediaFiles.setUrl("/" + bucketVideos + filePath);
        }
        if (StringUtils.isNoneBlank(uploadFileParamsDTO.getCurrentObjectName())) {
            mediaFiles.setBucket(bucketFiles);
            mediaFiles.setUrl(uploadFileParamsDTO.getCurrentObjectName());
        } else {
            filePath = getDateFilePath(fileName);
            mediaFiles.setBucket(bucketFiles);
            mediaFiles.setUrl("/" + bucketFiles + filePath);
        }
        mediaFiles.setFilename(uploadFileParamsDTO.getFileName());
        mediaFiles.setFilePath(fileName);
        mediaFiles.setStatus("1");
        mediaFiles.setCreateDate(LocalDateTime.now());
        mediaFilesMapper.insert(mediaFiles);
    }

    @Override
    public boolean checkFile(String md5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(md5);
        if (mediaFiles != null) {
            try {
                GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketVideos).object(mediaFiles.getFilePath()).build();
                FilterInputStream object = minioClient.getObject(objectArgs);
                return object != null;
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new XueChengPlusException("数据校验失败,服务器内部错误");
            }
        }
        return false;
    }

    @Override
    public boolean checkChunk(String md5, int chunk) {
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(bucketVideos)
                .object(getBucketMd5HexFilePath(md5) + "/chunk/" + chunk)
                .build();
        try {
            minioClient.getObject(objectArgs);
            return true;
        } catch (ErrorResponseException e) {
            if (checkMergeFileExiste(md5)) {
                return mediaFilesMapper.selectById(md5) != null;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean uploadChunk(String md5, int chunk, InputStream inputStream, String originalFileName) {

        putMinIoBucket(bucketVideos, inputStream, chunkFilePath(md5) + chunk, originalFileName);
        return true;
    }


    public MediaFiles getMediaFileById(String id) {
        return mediaFilesMapper.selectById(id);
    }

    private String getBucketMd5HexFilePath(String md5) {
        if (md5 == null || md5.length() < 10) {
            throw new XueChengPlusException("md5 parame exception");
        }
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5;
    }

    private String getmd5Hex(InputStream inputStream) {
        try {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException e) {
            throw new XueChengPlusException(e.getMessage());
        }
    }

    private String chunkFilePath(String md5) {
        return getBucketMd5HexFilePath(md5) + "/chunk/";
    }

    @Override
    public boolean mergeFile(Long companyId, String md5, int chunkTotal, UploadFileParamsDTO uploadFileParamsDTO) {
        String fileType = getFileType(uploadFileParamsDTO.getFileName());
        String mergeUrl = getBucketMd5HexFilePath(md5) + "/" + md5 + fileType;
        if (checkMergeFileExiste(md5)) return true;
        String chunckPath = chunkFilePath(md5);
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(chunkTotal).map(i ->
                ComposeSource.builder()
                        .bucket(bucketVideos)
                        .object(chunckPath + i)
                        .build()).collect(Collectors.toList());


        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs
                .builder()
                .bucket(bucketVideos)
                .object(mergeUrl)
                .sources(sources)
                .build();


        try {
            minioClient.composeObject(composeObjectArgs);
            long fileSize = verifyMD5Hex(mergeUrl, md5);
            clearChunk(md5, chunkTotal);
            uploadFileParamsDTO.setFileSize(fileSize);
            uploadFileParamsDTO.setFileType("001002");
            uploadFileParamsDTO.setTags("视频文件");
            uploadFileParamsDTO.setVideo(true);
            currentProxy.saveMediaDateBase(companyId, uploadFileParamsDTO, md5, mergeUrl);
            if (!fileType.equals(".mp4")) {
                MediaFiles mediaFileById = getMediaFileById(md5);
                currentProxy.saveMediaProcess(mediaFileById);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new XueChengPlusException("数据校验失败,服务器内部错误");
        }

        return true;
    }

    private boolean checkMergeFileExiste(String md5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(md5);
        if (mediaFiles != null) {
            GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketVideos).object(mediaFiles.getFilePath()).build();
            try {
                GetObjectResponse object = minioClient.getObject(objectArgs);
                if (object != null) {
                    return true;
                }
            } catch (InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new XueChengPlusException("内部错误");
            } catch (ErrorResponseException e) {
                log.warn(e.getMessage());
            }


        }
        return false;
    }


    private void clearChunk(String md5, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjectList = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFilePath(md5) + i))
                    .collect(Collectors.toList());
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(bucketVideos)
                    .objects(deleteObjectList)
                    .build();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(result -> {
                try {
                    result.get();
                } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                         InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                         XmlParserException e) {
                    log.error(e.getMessage());
                    throw new XueChengPlusException("内部错误");
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            XueChengPlusException.cast("内部错误");
        }
    }

    private Long verifyMD5Hex(String url, String md5) {
        GetObjectArgs testbucket = GetObjectArgs
                .builder()
                .bucket(bucketVideos)
                .object(url)
                .build();


        try {
            GetObjectResponse object = minioClient.getObject(testbucket);
            String md5Hex = DigestUtils.md5Hex(object);
            if (!md5Hex.equals(md5)) {
                throw new XueChengPlusException("文件最终上传失败，文件出现错误");
            }
            byte[] bytes = new byte[1024];
            int bufferLen;
            long length = 0L;
            while ((bufferLen = object.read(bytes)) != -1) {
                length += bufferLen;
            }
            return length;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new XueChengPlusException("文件最终上传失败");
        }

    }

    @Override
    public File downloadFile(String url, String bucket) {
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(url)
                .build();

        try {
            InputStream inputStream = minioClient.getObject(objectArgs);
            String type = url.substring(url.lastIndexOf("."));

            int len;
            byte[] bytes = new byte[1024];
            File tempFile = File.createTempFile("minio", type);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(tempFile.getPath())));
            while ((len = inputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes, 0, len);
                bufferedOutputStream.flush();
            }
            return tempFile;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMimeType(String extension) {
        if (extension == null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        } else if (extension.contains("html")) {
            mimeType = MediaType.TEXT_HTML_VALUE;
        }
        return mimeType;
    }


    @Override
    public String uploadFile(InputStream inputStream, String originalFileName, String md5, String fileType) {
        String md5HexFilePath = getBucketMd5HexFilePath(md5);

        String object = md5HexFilePath + "/" + md5 + fileType;

        putMinIoBucket(bucketVideos, inputStream, object, originalFileName);
        return object;
    }

    @Override
    public String getMediaURL(String mediaId) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
        if (mediaFiles == null) {
            XueChengPlusException.cast("媒资不存在");
        }
        if (StringUtils.isBlank(mediaFiles.getUrl())) {
            XueChengPlusException.cast("视频正在处理");
        }
        return mediaFiles.getUrl();
    }
}

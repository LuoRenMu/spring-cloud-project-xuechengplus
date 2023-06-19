package cn.lomu.media.service;

import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.media.model.dto.QueryMediaParamsDto;
import cn.lomu.media.model.dto.UploadFileParamsDTO;
import cn.lomu.media.model.po.MediaFiles;

import java.io.File;
import java.io.InputStream;


public interface MediaFileService {


    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    MediaFiles uploadFile(Long commpanyId, UploadFileParamsDTO uploadFileParamsDTO, String objectName, InputStream inputStream);

    void saveMediaDateBase(Long companyId, UploadFileParamsDTO uploadFileParamsDTO, String hex, String fileName);

    boolean checkFile(String md5);

    boolean checkChunk(String md5, int chunk);

    boolean uploadChunk(String md5, int chunk, InputStream inputStream, String originalFileName);

    boolean mergeFile(Long companyId, String md5, int chunkTotal, UploadFileParamsDTO uploadFileParamsDTO);

    void saveMediaProcess(MediaFiles mediaFiles);

    File downloadFile(String url, String bucket);

    String uploadFile(InputStream inputStream, String originalFileName, String md5, String fileType);

    String getMediaURL(String mediaId);
}

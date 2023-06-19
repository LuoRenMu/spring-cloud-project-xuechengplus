package cn.lomu.media.controller;


import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.media.model.dto.QueryMediaParamsDto;
import cn.lomu.media.model.dto.UploadFileParamsDTO;
import cn.lomu.media.model.dto.UploadFileResultDTO;
import cn.lomu.media.model.po.MediaFiles;
import cn.lomu.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(@RequestParam(required = false) PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        if (pageParams == null) {
            pageParams = new PageParams();
        }
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MediaFiles uploadFile(@RequestPart("filedata") MultipartFile filedata, @RequestPart(required = false) String obejctName) {
        long companyId = 123L;
        try {
            UploadFileParamsDTO uploadFileParamsDTO = new UploadFileParamsDTO();
            uploadFileParamsDTO.setFileName(filedata.getOriginalFilename());
            uploadFileParamsDTO.setFileType(filedata.getContentType());
            uploadFileParamsDTO.setFileType("001001");
            uploadFileParamsDTO.setFileSize(filedata.getSize());
            if (StringUtils.isNoneBlank(obejctName)) {
                uploadFileParamsDTO.setFileName(obejctName.substring(obejctName.lastIndexOf("/")));
            }
            InputStream inputStream = filedata.getInputStream();
            return mediaFileService.uploadFile(companyId, uploadFileParamsDTO, obejctName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

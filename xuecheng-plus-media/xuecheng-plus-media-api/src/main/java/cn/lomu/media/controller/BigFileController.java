package cn.lomu.media.controller;

import cn.lomu.base.model.RestResponse;
import cn.lomu.media.model.dto.UploadFileParamsDTO;
import cn.lomu.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author LoMu
 * Date  2023-05-22 7:32
 */
@RestController
public class BigFileController {
    @Autowired
    MediaFileService mediaFileService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) {
        return RestResponse.success(mediaFileService.checkFile(fileMd5));
    }

    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadchunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunk") int chunk
    ) {

        try {
            return RestResponse.success(mediaFileService.uploadChunk(fileMd5, chunk, file.getInputStream(), file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergechunks(@RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("fileName") String fileName,
                                             @RequestParam("chunkTotal") int chunkTotal) {

        Long companyId = 1232141425L;

        UploadFileParamsDTO uploadFileParamsDto = new UploadFileParamsDTO();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFileName(fileName);

        return RestResponse.success(mediaFileService.mergeFile(companyId, fileMd5, chunkTotal, uploadFileParamsDto));

    }

    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) {
        return RestResponse.success(mediaFileService.checkChunk(fileMd5, chunk));
    }


}

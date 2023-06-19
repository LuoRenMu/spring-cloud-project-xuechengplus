package cn.lomu.media.controller;

import cn.lomu.base.model.RestResponse;
import cn.lomu.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LoMu
 * Date  2023-06-04 3:31
 */
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    MediaFileService mediaFileService;

    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId) {
        String mediaURL = mediaFileService.getMediaURL(mediaId);
        return RestResponse.success(mediaURL);
    }

}

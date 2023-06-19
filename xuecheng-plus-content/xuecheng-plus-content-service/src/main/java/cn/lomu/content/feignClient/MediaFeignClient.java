package cn.lomu.content.feignClient;


import cn.lomu.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LoMu
 * Date  2023-06-13 10:29
 */

@FeignClient(value = "media-api/media", configuration = MultipartSupportConfig.class, fallbackFactory = MediaFeignClientFallbackFactory.class)
public interface MediaFeignClient {
    @GetMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadFile(@RequestPart("filedata") MultipartFile filedata, @RequestPart(required = false, value = "obejctName") String obejctName);

    @GetMapping(value = "/test")
    String test();
}

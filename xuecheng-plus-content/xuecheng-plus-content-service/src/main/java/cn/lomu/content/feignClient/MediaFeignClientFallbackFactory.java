package cn.lomu.content.feignClient;


import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author LoMu
 * Date  2023-06-14 9:32
 */
@Slf4j
public class MediaFeignClientFallbackFactory implements FallbackFactory<MediaFeignClient> {
    @Override
    public MediaFeignClient create(Throwable throwable) {
        return new MediaFeignClient() {
            @Override
            public void uploadFile(MultipartFile filedata, String obejctName) {
                log.debug("降级处理:{}", throwable.toString());
            }

            @Override
            public String test() {
                return null;
            }
        };
    }
}

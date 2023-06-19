package cn.lomu.content.feignClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LoMu
 * Date  2023-06-18 20:47
 */

@FeignClient(value = "search/search")
public interface SearchFeignClient {
    @PostMapping("/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex);
}

package cn.lomu.content.service;

import cn.lomu.content.feignClient.CourseIndex;
import cn.lomu.content.model.dto.CoursePreviewDTO;
import cn.lomu.content.model.po.CoursePublish;

import com.baomidou.mybatisplus.extension.service.IService;


import java.io.File;

/**
 * @author LoMu
 * Date  2023-06-03 19:10
 */
public interface CoursePublishService extends IService<CoursePublish> {
    CoursePreviewDTO getCoursePreviewInfo(Long id);

    void publish(Long companyId, Long id);

    void uploadCourseHTML(Long id);

    boolean addElastic(CourseIndex index);


}

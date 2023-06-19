package cn.lomu.content.controller;

import cn.lomu.content.model.dto.CoursePreviewDTO;
import cn.lomu.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LoMu
 * Date  2023-06-03 20:17
 */
@RestController
@RequestMapping("/open")
public class CourseOpenController {
    @Autowired
    CoursePublishService coursePublishService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDTO getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        return coursePublishService.getCoursePreviewInfo(courseId);
    }

}

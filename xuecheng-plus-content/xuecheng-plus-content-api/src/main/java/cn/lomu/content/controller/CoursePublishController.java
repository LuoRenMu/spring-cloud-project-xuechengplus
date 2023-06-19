package cn.lomu.content.controller;

import cn.lomu.content.model.dto.CoursePreviewDTO;
import cn.lomu.content.service.CoursePublishPreService;
import cn.lomu.content.service.CoursePublishService;
import cn.lomu.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author LoMu
 * Date  2023-06-02 17:04
 */

@Controller
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    CoursePublishPreService coursePublishPreService;


    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView coursepreview(@PathVariable Long courseId) {
        ModelAndView modelAndView = new ModelAndView();

        CoursePreviewDTO coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }


    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        Long companyId = 1232141425L;
        coursePublishPreService.commitAudit(companyId, courseId);
    }


    @PostMapping("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId) {
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId, courseId);
    }


}

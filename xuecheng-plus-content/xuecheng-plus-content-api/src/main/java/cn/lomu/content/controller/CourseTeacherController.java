package cn.lomu.content.controller;

import cn.lomu.content.model.dto.AddCourseTeacherDTO;
import cn.lomu.content.model.po.CourseTeacher;
import cn.lomu.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-13 6:29
 */

@RestController
@Api("课程教师")
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> courseTeacherList(@PathVariable Long courseId) {
        return courseTeacherService.courseTeacherList(courseId);
    }

    @PostMapping("/courseTeacher")
    public CourseTeacher addCourseTeacher(@Validated @RequestBody AddCourseTeacherDTO addCourseTeacherDTO) {
        return courseTeacherService.addCourseTeacher(addCourseTeacherDTO);
    }

    @PutMapping("/courseTeacher")
    public CourseTeacher updateCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.updateCourseTeacher(courseTeacher);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable @NotNull Long courseId, @PathVariable @NotNull Long teacherId) {
        courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }
}

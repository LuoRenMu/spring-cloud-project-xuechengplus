package cn.lomu.content.service;

import cn.lomu.content.model.dto.AddCourseTeacherDTO;
import cn.lomu.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-13 6:32
 */
public interface CourseTeacherService {
    List<CourseTeacher> courseTeacherList(Long id);

    CourseTeacher addCourseTeacher(AddCourseTeacherDTO addCourseTeacherDTO);

    CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher);

    void deleteCourseTeacher(Long courseId, Long teacherId);
}

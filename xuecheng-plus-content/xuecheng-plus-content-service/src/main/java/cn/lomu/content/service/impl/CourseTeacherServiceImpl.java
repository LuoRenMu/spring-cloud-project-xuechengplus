package cn.lomu.content.service.impl;

import cn.lomu.base.exception.XueChengPlusException;
import cn.lomu.content.mapper.CourseTeacherMapper;
import cn.lomu.content.model.dto.AddCourseTeacherDTO;
import cn.lomu.content.model.po.CourseTeacher;
import cn.lomu.content.service.CourseTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-13 6:32
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> courseTeacherList(Long id) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, id);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher addCourseTeacher(AddCourseTeacherDTO addCourseTeacherDTO) {
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(addCourseTeacherDTO, courseTeacher);
        if (addCourseTeacherDTO.getId() != null) {
            courseTeacherMapper.updateById(courseTeacher);
        } else {
            courseTeacherMapper.insert(courseTeacher);
        }
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher) {
        CourseTeacher courseTeacherDB = courseTeacherMapper.selectById(courseTeacher.getId());
        if (courseTeacherDB == null) {
            throw new XueChengPlusException("更新数据id不存在");
        }
        courseTeacherMapper.updateById(courseTeacher);
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        lambdaQueryWrapper.eq(CourseTeacher::getId, teacherId);
        courseTeacherMapper.delete(lambdaQueryWrapper);
    }
}

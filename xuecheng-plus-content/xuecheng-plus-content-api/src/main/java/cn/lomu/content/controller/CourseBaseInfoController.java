package cn.lomu.content.controller;

import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.content.model.dto.AddCourseDto;
import cn.lomu.content.model.dto.CourseBaseInfoDto;
import cn.lomu.content.model.dto.EditCourseDTO;
import cn.lomu.content.model.dto.QueryCourseParamsDto;
import cn.lomu.content.model.po.CourseBase;
import cn.lomu.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author LoMu
 * Date  2023-05-01 11:17
 */

@RestController
@Api(value = "CourseBaseInfoInterface", tags = "CourseBaseInfoInterface")
public class CourseBaseInfoController {
    @Autowired
    CourseBaseService courseBaseService;

    @PostMapping("/course/list")
    public PageResult<CourseBase> courseBasePageResult(
            PageParams pageParams,
            @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto
    ) {

        return courseBaseService.queryCourseBaseList(pageParams, queryCourseParamsDto);
    }

    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@Validated @RequestBody AddCourseDto addCourseDto) {
        return courseBaseService.createCourseBase(123L, addCourseDto);
    }

    @ApiOperation(value = "根据id查询课程")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseInfo(@PathVariable Long courseId) {
        return courseBaseService.getCourseBaseInfoDto(courseId);
    }

    @ApiOperation(value = "修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourseBaseInfo(@Validated @RequestBody EditCourseDTO editCourseDTO) {

        return courseBaseService.updateCourseBaseInfo(1232141425L, editCourseDTO);
    }

    @DeleteMapping("/course/{id}")
    public void deleteCourseBase(@PathVariable Long id) {
        courseBaseService.deleteCourseBase(id);
    }


}

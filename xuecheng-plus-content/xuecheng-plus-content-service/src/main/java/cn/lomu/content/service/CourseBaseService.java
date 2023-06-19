package cn.lomu.content.service;


import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.content.model.dto.AddCourseDto;
import cn.lomu.content.model.dto.CourseBaseInfoDto;
import cn.lomu.content.model.dto.EditCourseDTO;
import cn.lomu.content.model.dto.QueryCourseParamsDto;
import cn.lomu.content.model.po.CourseBase;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author LoMu
 * Date  2023-05-03 17:24
 */

public interface CourseBaseService extends IService<CourseBase> {

    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    CourseBaseInfoDto createCourseBase(Long commpanyId, AddCourseDto addCourseDto);

    CourseBaseInfoDto getCourseBaseInfoDto(Long id);

    CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDTO editCourseDTO);

    void deleteCourseBase(Long id);
}

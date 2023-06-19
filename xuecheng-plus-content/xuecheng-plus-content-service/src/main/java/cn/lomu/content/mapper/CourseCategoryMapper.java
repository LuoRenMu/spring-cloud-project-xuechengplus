package cn.lomu.content.mapper;

import cn.lomu.content.model.dto.CourseCategoryTreeDTO;
import cn.lomu.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    List<CourseCategoryTreeDTO> selectTreeNodes(String id);

}

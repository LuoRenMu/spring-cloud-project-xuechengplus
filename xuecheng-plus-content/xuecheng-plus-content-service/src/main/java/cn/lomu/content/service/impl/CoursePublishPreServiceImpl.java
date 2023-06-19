package cn.lomu.content.service.impl;

import cn.lomu.base.exception.XueChengPlusException;

import cn.lomu.content.mapper.CoursePublishPreMapper;
import cn.lomu.content.model.dto.CourseBaseInfoDto;
import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.model.po.CourseBase;
import cn.lomu.content.model.po.CourseMarket;
import cn.lomu.content.model.po.CoursePublishPre;
import cn.lomu.content.service.CourseBaseService;
import cn.lomu.content.service.CoursePublishPreService;
import cn.lomu.content.service.TeachplanService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LoMu
 * Date  2023-06-07 14:08
 */
@Service
public class CoursePublishPreServiceImpl extends ServiceImpl<CoursePublishPreMapper, CoursePublishPre> implements CoursePublishPreService {
    @Autowired
    CourseBaseService courseBaseService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;


    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = courseBaseService.getCourseBaseInfoDto(courseId);
        if (courseBaseInfoDto == null) {
            XueChengPlusException.cast("课程不存在");
        }
        if (courseBaseInfoDto.getPic() == null || StringUtils.isBlank(courseBaseInfoDto.getPic())) {
            XueChengPlusException.cast("图片不可为空");
        }

        List<TeachplanDTO> teachplanDTOS = teachplanService.selectTreeNodes(courseId);
        if (teachplanDTOS == null || teachplanDTOS.size() == 0) {
            XueChengPlusException.cast("课程计划不可为空");
        }

        if (!courseBaseInfoDto.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("你没有权限");
        }


        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(courseBaseInfoDto, courseMarket);

        String teachplanJson = JSON.toJSONString(teachplanDTOS);
        String courseMarketJson = JSON.toJSONString(courseMarket);

        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfoDto, coursePublishPre);
        coursePublishPre.setId(courseBaseInfoDto.getId());
        coursePublishPre.setStatus(courseBaseInfoDto.getAuditStatus());
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setTeachplan(teachplanJson);
        coursePublishPre.setMarket(courseMarketJson);

        CoursePublishPre coursePublishPreDateBase = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreDateBase != null && courseBaseInfoDto.getAuditStatus().equals("202003")) {
            coursePublishPreMapper.updateById(coursePublishPre);
        } else {
            CourseBase courseBase = new CourseBase();
            BeanUtils.copyProperties(courseBaseInfoDto, courseBase);
            courseBase.setAuditStatus("202003");
            courseBaseService.updateById(courseBase);
            coursePublishPreMapper.insert(coursePublishPre);
        }
    }
}

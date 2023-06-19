package cn.lomu.content.service.impl;

import cn.lomu.base.exception.XueChengPlusException;
import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.content.mapper.CourseBaseMapper;
import cn.lomu.content.mapper.CourseCategoryMapper;
import cn.lomu.content.mapper.CourseMarketMapper;
import cn.lomu.content.model.dto.AddCourseDto;
import cn.lomu.content.model.dto.CourseBaseInfoDto;
import cn.lomu.content.model.dto.EditCourseDTO;
import cn.lomu.content.model.dto.QueryCourseParamsDto;
import cn.lomu.content.model.po.CourseBase;
import cn.lomu.content.model.po.CourseCategory;
import cn.lomu.content.model.po.CourseMarket;
import cn.lomu.content.service.CourseBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


/**
 * @author LoMu
 * Date  2023-05-03 17:27
 */
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {
    @Autowired
    public CourseBaseMapper courseBaseMapper;

    @Autowired
    public CourseMarketMapper courseMarketMapper;

    @Autowired
    public CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(
                StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()),
                CourseBase::getName
                , queryCourseParamsDto.getCourseName()
        );
        queryWrapper.eq(
                StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus()
        );
        queryWrapper.eq(
                StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus()
        );

        IPage<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        IPage<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getPages());
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long commpanyId, AddCourseDto addCourseDto) {
        CourseBase courseBase = new CourseBase();
        CourseMarket courseMarket = new CourseMarket();

        BeanUtils.copyProperties(addCourseDto, courseBase);
        BeanUtils.copyProperties(addCourseDto, courseMarket);

        courseBase.setCompanyId(commpanyId);
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        courseBase.setCreateDate(LocalDateTime.now());

        int insert = courseBaseMapper.insert(courseBase);
        courseMarket.setId(courseBase.getId());
        if (insert < 0 || !(saveCourseMarket(courseMarket))) {
            throw new RuntimeException("课程保存失败");
        }

        return getCourseBaseInfoDto(courseBase.getId());
    }

    public boolean saveCourseMarket(CourseMarket courseMarket) {
        if (courseMarket.getCharge().equals("201001") && courseMarket.getPrice() <= 0) {
            throw new XueChengPlusException("收费情况下价格不可为空");
        }
        if (courseMarket.getCharge().equals("201000")) {
            courseMarket.setPrice(0f);
        }
        CourseMarket courseMarketDataBase = courseMarketMapper.selectById(courseMarket.getId());
        int insert;
        if (courseMarketDataBase == null) {
            insert = courseMarketMapper.insert(courseMarket);
        } else {
            insert = courseMarketMapper.updateById(courseMarket);
        }
        return insert > 0;

    }

    @Override
    public CourseBaseInfoDto getCourseBaseInfoDto(Long id) {
        CourseBase courseBase = courseBaseMapper.selectById(id);
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        if (courseBase != null) {
            BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
            String st = courseBase.getSt();
            String mt = courseBase.getMt();
            Optional<CourseCategory> stOptional = Optional.of(courseCategoryMapper.selectById(st));
            Optional<CourseCategory> mtOptional = Optional.of(courseCategoryMapper.selectById(mt));

            stOptional.ifPresent(courseCategory -> courseBaseInfoDto.setStName(courseCategory.getName()));
            mtOptional.ifPresent(courseCategory -> courseBaseInfoDto.setMtName(courseCategory.getName()));

        } else {
            return null;
        }
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDTO editCourseDTO) {
        Long courseId = editCourseDTO.getId();
        CourseBase courseBaseDB = courseBaseMapper.selectById(courseId);

        if (courseBaseDB != null) {
            if (!Objects.equals(courseBaseDB.getCompanyId(), companyId)) {
                throw new XueChengPlusException("无权执行此操作");
            }
            CourseBase courseBase = new CourseBase();
            CourseMarket courseMarket = new CourseMarket();
            BeanUtils.copyProperties(editCourseDTO, courseBase);
            BeanUtils.copyProperties(editCourseDTO, courseMarket);

            if (!(courseBaseMapper.updateById(courseBase) > 0)) {
                throw new XueChengPlusException("更新失败");
            }
            if (!(saveCourseMarket(courseMarket))) {
                throw new XueChengPlusException("更新失败");
            }
        } else {
            throw new XueChengPlusException("更新课程不存在");
        }

        return getCourseBaseInfoDto(courseId);
    }

    @Override
    public void deleteCourseBase(Long id) {
        courseBaseMapper.deleteById(id);
    }

}


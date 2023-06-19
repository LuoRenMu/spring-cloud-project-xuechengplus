package cn.lomu.content.service.impl;

import cn.lomu.base.exception.XueChengPlusException;
import cn.lomu.content.mapper.CourseBaseMapper;
import cn.lomu.content.mapper.TeachplanMapper;
import cn.lomu.content.mapper.TeachplanMediaMapper;
import cn.lomu.content.model.dto.BindTeachplanMediaDTO;
import cn.lomu.content.model.dto.SaveTeachplanDTO;
import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.model.po.CourseBase;
import cn.lomu.content.model.po.Teachplan;
import cn.lomu.content.model.po.TeachplanMedia;
import cn.lomu.content.service.TeachplanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LoMu
 * Date  2023-05-12 2:19
 */

@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;


    @Override
    public List<TeachplanDTO> selectTreeNodes(long courseId) {
        List<TeachplanDTO> teachplanDTOS = teachplanMapper.selectTreeNodes(courseId);
        Map<Long, TeachplanDTO> teachplanDTOMap = teachplanDTOS.stream().collect(Collectors.toMap(Teachplan::getId, value -> value));
        List<TeachplanDTO> parentList = new ArrayList<>();
        teachplanDTOS.forEach(teachplanDTO -> {
            TeachplanDTO parent = teachplanDTOMap.get(teachplanDTO.getParentid());
            if (parent != null) {
                if (parent.getTeachPlanTreeNodes() == null) {
                    List<TeachplanDTO> list = new ArrayList<>();
                    list.add(teachplanDTO);
                    parent.setTeachPlanTreeNodes(list);
                } else {
                    parent.getTeachPlanTreeNodes().add(teachplanDTO);
                }
            } else {
                parentList.add(teachplanDTO);
            }
        });
        return parentList;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDTO saveTeachplanDTO) {
        if (saveTeachplanDTO.getId() != null) {
            Teachplan teachplanDB = teachplanMapper.selectById(saveTeachplanDTO.getId());
            if (teachplanDB == null) {
                throw new XueChengPlusException("更新数据id不存在");
            }
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDTO, teachplan);

            teachplanMapper.updateById(teachplan);
        } else {
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDTO, teachplan);
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId, saveTeachplanDTO.getCourseId());
            long parentId = saveTeachplanDTO.getParentid() == null ? 0 : saveTeachplanDTO.getParentid();
            queryWrapper.eq(Teachplan::getParentid, parentId);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long id) {

        Teachplan teachplan = verifyNotNull(id);
        //TODO 判断用户权限
        CourseBase courseBase = courseBaseMapper.selectById(teachplan.getCourseId());
        Long companyId = courseBase.getCompanyId();

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        if (teachplan.getParentid() == null || teachplan.getParentid() == 0) {
            queryWrapper.eq(Teachplan::getId, id)
                    .or()
                    .eq(Teachplan::getParentid, id);
        } else {
            queryWrapper.eq(Teachplan::getId, id);
        }
        teachplanMapper.delete(queryWrapper);

    }

    @Override
    @Transactional
    public void moveUp(Long id) {
        moveTeachplan(id, 1);
    }

    private void moveTeachplan(Long id, int move) {
        Teachplan teachplan = verifyNotNull(id);

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        queryWrapper.eq(Teachplan::getOrderby, teachplan.getOrderby() - move);
        Teachplan teachplanMove = teachplanMapper.selectOne(queryWrapper);
        if (teachplanMove != null) {
            teachplanMove.setOrderby(teachplanMove.getOrderby() + move);
            teachplanMapper.updateById(teachplanMove);
        } else {
            return;
        }

        teachplan.setOrderby(teachplan.getOrderby() - move);
        teachplanMapper.updateById(teachplan);
    }

    private Teachplan verifyNotNull(Long id) {
        Optional<Teachplan> teachplanOptional = Optional.ofNullable(teachplanMapper.selectById(id));
        return teachplanOptional.orElseThrow(() -> new XueChengPlusException("课程计划不存在"));
    }


    @Override
    public void moveDown(Long id) {
        moveTeachplan(id, -1);
    }

    @Override
    public void associationMedia(BindTeachplanMediaDTO bindTeachplanMediaDTO) {
        Long teachplanId = bindTeachplanMediaDTO.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            XueChengPlusException.cast("教学计划不存在");
        }
        Long courseId = teachplan.getCourseId();
        Integer grade = teachplan.getGrade();
        if (grade != 2) {
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
        TeachplanMedia select = teachplanMediaMapper.selectOne(queryWrapper);
        if (select != null) {
            int delete = teachplanMediaMapper.delete(queryWrapper);
            if (!(delete > 0)) {
                XueChengPlusException.cast("重新绑定媒体错误");
            }
        }


        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDTO.getFileName());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMedia.setMediaId(bindTeachplanMediaDTO.getMediaId());
        //TODO:应当验证media是否存在于数据库(使用RPC)
        int insert = teachplanMediaMapper.insert(teachplanMedia);
        if (!(insert > 0)) {
            XueChengPlusException.cast("重写绑定媒体错误");
        }

    }

    @Override
    public void deleteMedia(long teachPlanId, long mediaId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId)
                .and(qw -> qw.eq(TeachplanMedia::getMediaId, mediaId));
        teachplanMediaMapper.delete(queryWrapper);
    }
}

package cn.lomu.content.mapper;


import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface TeachplanMapper extends BaseMapper<Teachplan> {

    List<TeachplanDTO> selectTreeNodes(long courseId);

}

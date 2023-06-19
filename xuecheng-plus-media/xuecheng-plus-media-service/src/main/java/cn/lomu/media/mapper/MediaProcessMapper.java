package cn.lomu.media.mapper;

import cn.lomu.media.model.po.MediaProcess;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    List<MediaProcess> selectShardTask(@Param("total") int total, @Param("index") int index, @Param("limit") int limit);

    int updateTaskStatus(@Param("id") long id);


}

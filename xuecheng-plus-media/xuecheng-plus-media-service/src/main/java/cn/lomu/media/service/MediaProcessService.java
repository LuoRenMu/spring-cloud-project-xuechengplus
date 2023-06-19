package cn.lomu.media.service;

import cn.lomu.media.model.po.MediaProcess;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-29 2:57
 */

public interface MediaProcessService extends IService<MediaProcess> {
    List<MediaProcess> selectShardTask(int total, int index, int limit);

    boolean updateTaskStatus(long id);

    List<MediaProcess> selectTimeOutData();

}

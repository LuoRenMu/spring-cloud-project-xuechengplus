package cn.lomu.media.service.impl;

import cn.lomu.media.mapper.MediaProcessMapper;
import cn.lomu.media.model.po.MediaProcess;
import cn.lomu.media.service.MediaProcessService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-29 2:59
 */
@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {
    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Override
    public List<MediaProcess> selectShardTask(int total, int index, int limit) {
        return mediaProcessMapper.selectShardTask(total, index, limit);
    }

    @Override
    public boolean updateTaskStatus(long id) {
        return mediaProcessMapper.updateTaskStatus(id) >= 1;
    }


    @Override
    public List<MediaProcess> selectTimeOutData() {
        LambdaQueryWrapper<MediaProcess> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaProcess::getStatus, "4");
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectList(queryWrapper);
        List<MediaProcess> timeOutList = new ArrayList<>();
        for (MediaProcess mediaProcess : mediaProcesses) {
            LocalDateTime date = mediaProcess.getCreateDate();
            if (date.plusMinutes(30).isAfter(LocalDateTime.now())) {
                timeOutList.add(mediaProcess);
            }
        }
        return timeOutList;
    }


}

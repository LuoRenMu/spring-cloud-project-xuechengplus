package cn.lomu.media.service.impl;

import cn.lomu.media.mapper.MediaProcessHistoryMapper;
import cn.lomu.media.mapper.MediaProcessMapper;
import cn.lomu.media.model.po.MediaProcess;
import cn.lomu.media.model.po.MediaProcessHistory;
import cn.lomu.media.service.MediaProcessHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author LoMu
 * Date  2023-05-29 20:46
 */
@Service
public class MediaProcessHistoryServiceImpl implements MediaProcessHistoryService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    @Transactional
    public void saveProcessFinishStatus(Long id, String status, String url, String errormsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(id);
        if (mediaProcess == null) {
            return;
        }
        if (status.equals("3")) {
            mediaProcess.setErrormsg(errormsg);
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcess.setStatus("3");
            mediaProcessMapper.updateById(mediaProcess);
            return;
        }


        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistory.setStatus(status);
        mediaProcessHistory.setUrl(url);
        mediaProcessHistory.setFinishDate(LocalDateTime.now());
        mediaProcessHistory.setId(null);


        if (!(mediaProcessHistoryMapper.insert(mediaProcessHistory) > 0)) {
            throw new RuntimeException("内部错误");
        }
        if (!(mediaProcessMapper.deleteById(mediaProcess) > 0)) {
            throw new RuntimeException("内部错误");
        }
    }
}

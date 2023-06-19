package cn.lomu.media.task;

import cn.lomu.media.model.po.MediaProcess;
import cn.lomu.media.service.MediaProcessService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-31 19:01
 */

@Component
public class ListenTask {
    @Autowired
    MediaProcessService mediaProcessService;

    @XxlJob("listenTimeOutJob")
    public void listenTimeOutJob() {
        List<MediaProcess> mediaProcesses = mediaProcessService.selectTimeOutData();
        for (MediaProcess mediaProcess : mediaProcesses) {
            mediaProcess.setStatus("3");
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcess.setErrormsg("timeOut");
        }
        mediaProcessService.updateBatchById(mediaProcesses);
    }
}

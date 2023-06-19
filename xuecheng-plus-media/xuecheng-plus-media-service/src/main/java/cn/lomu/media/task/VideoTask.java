package cn.lomu.media.task;

import cn.lomu.base.utils.Mp4VideoUtil;
import cn.lomu.media.model.po.MediaProcess;
import cn.lomu.media.service.MediaFileService;
import cn.lomu.media.service.MediaProcessHistoryService;
import cn.lomu.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author LoMu
 * Date  2023-05-26 15:27
 */


@Slf4j
@Component
public class VideoTask {

    @Autowired
    MediaProcessService mediaProcessService;

    @Autowired
    MediaProcessHistoryService mediaProcessHistoryService;

    @Autowired
    MediaFileService mediaFileService;

    int processors = Runtime.getRuntime().availableProcessors();

    ExecutorService executorService = Executors.newFixedThreadPool(processors);


    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegPath;

    @com.xxl.job.core.handler.annotation.XxlJob("shardingJobHandler")
    public void shardingJobHandler() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaProcess> mediaProcesses = mediaProcessService.selectShardTask(shardTotal, shardIndex, processors);
        if (mediaProcesses.size() == 0) {
            return;
        }
        CountDownLatch count = new CountDownLatch(processors);
        for (MediaProcess mediaProcess : mediaProcesses) {
            executorService.execute(() -> {
                try {
                    boolean b = mediaProcessService.updateTaskStatus(mediaProcess.getId());
                    if (!b) {
                        log.warn("未获取:{}", mediaProcess.getId());
                        return;
                    }
                    File file = mediaFileService.downloadFile(mediaProcess.getFilePath(), mediaProcess.getBucket());
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    String filePath = "D:\\SecurityFile\\" + uuid + ".mp4";
                    File mp4File = new File(filePath);
                    FileInputStream inputStream = new FileInputStream(mp4File);
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegPath, file.getPath(), filePath);
                    String message = mp4VideoUtil.generateMp4();
                    if (message.equals("success")) {
                        String url;
                        try {
                            url = mediaFileService.uploadFile(inputStream, mp4File.getName(), mediaProcess.getFileId(), ".mp4");
                        } catch (Exception e) {
                            log.error("视频处理任务失败:上传转码后文件失败");
                            mediaProcessHistoryService.saveProcessFinishStatus(mediaProcess.getId(), "3", null, "上传转码后文件失败");
                            throw new RuntimeException(e);
                        }
                        mediaProcessHistoryService.saveProcessFinishStatus(mediaProcess.getId(), "2", url, null);
                    } else {
                        log.error("视频处理任务失败{}", message);
                        mediaProcessHistoryService.saveProcessFinishStatus(mediaProcess.getId(), "3", null, message);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    count.countDown();
                }
            });
        }


        try {
            count.await(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

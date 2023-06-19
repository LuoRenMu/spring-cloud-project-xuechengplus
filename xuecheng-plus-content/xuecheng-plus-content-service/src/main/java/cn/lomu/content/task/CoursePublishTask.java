package cn.lomu.content.task;

import cn.lomu.content.config.MultipartSupportConfig;
import cn.lomu.content.feignClient.CourseIndex;
import cn.lomu.content.model.po.CoursePublish;
import cn.lomu.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LoMu
 * Date  2023-06-13 6:31
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    CoursePublishService coursePublishService;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex, shardTotal, "course_publish", 30, 60);

    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        String id = mqMessage.getBusinessKey1();
        generateCourseHTML(Long.parseLong(id));

        generateElatsic(Long.parseLong(id));
        //block code completion


        return true;
    }

    private void generateElatsic(Long id) {
        CoursePublish coursePublish = coursePublishService.getById(id);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        coursePublishService.addElastic(courseIndex);
    }

    private void generateCourseHTML(Long id) {
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne == 1) {
            log.debug("stageOne Completed");
            return;
        }
        coursePublishService.uploadCourseHTML(id);

    }
}

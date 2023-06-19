package cn.lomu.content.service.impl;

import cn.lomu.base.exception.CommonError;
import cn.lomu.base.exception.XueChengPlusException;
import cn.lomu.content.config.MultipartSupportConfig;
import cn.lomu.content.feignClient.CourseIndex;
import cn.lomu.content.feignClient.MediaFeignClient;
import cn.lomu.content.feignClient.SearchFeignClient;
import cn.lomu.content.mapper.CoursePublishMapper;
import cn.lomu.content.model.dto.CourseBaseInfoDto;
import cn.lomu.content.model.dto.CoursePreviewDTO;
import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.model.po.CourseBase;
import cn.lomu.content.model.po.CoursePublish;
import cn.lomu.content.model.po.CoursePublishPre;
import cn.lomu.content.service.CourseBaseService;
import cn.lomu.content.service.CoursePublishPreService;
import cn.lomu.content.service.CoursePublishService;
import cn.lomu.content.service.TeachplanService;
import cn.lomu.content.utils.FreeMakerUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author LoMu
 * Date  2023-06-03 19:10
 */

@Service
@Slf4j
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {
    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseService courseBaseService;

    @Autowired
    CoursePublishPreService coursePublishPreService;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaFeignClient mediaFeignClient;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Override
    public CoursePreviewDTO getCoursePreviewInfo(Long id) {
        List<TeachplanDTO> list = teachplanService.selectTreeNodes(id);
        CourseBaseInfoDto courseBaseInfoDto = courseBaseService.getCourseBaseInfoDto(id);
        CoursePreviewDTO coursePreviewDTO = new CoursePreviewDTO();
        coursePreviewDTO.setCourseBase(courseBaseInfoDto);
        coursePreviewDTO.setTeachplans(list);
        return coursePreviewDTO;
    }

    @Override
    @Transactional
    public void publish(Long companyId, Long id) {
        CoursePublishPre coursePublishPre = coursePublishPreService.getById(id);
        if (coursePublishPre == null) {
            throw new XueChengPlusException("预发布课程不存在");
        }
        CourseBase courseBase = courseBaseService.getById(id);
        if (courseBase == null) {
            throw new XueChengPlusException("课程不存在");
        }
        if (!courseBase.getAuditStatus().equals("202004")) {
            throw new XueChengPlusException("审核未通过");
        }
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            throw new XueChengPlusException("无权操作");
        }

        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setOnlineDate(LocalDateTime.now());
        CoursePublish publishDateBase = coursePublishMapper.selectById(id);
        if (publishDateBase != null) {
            coursePublishMapper.updateById(coursePublish);
        } else {
            coursePublishMapper.insert(coursePublish);
        }
        courseBase.setStatus("203002");
        courseBase.setAuditStatus("202003");

        courseBaseService.updateById(courseBase);

        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(id), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
        coursePublishPreService.removeById(id);
    }

    @Override
    public void uploadCourseHTML(Long id) {
        //storage to oss
        Map<String, CoursePreviewDTO> hashMap = new HashMap<>();
        CoursePreviewDTO coursePreviewInfo = getCoursePreviewInfo(id);
        hashMap.put("model", coursePreviewInfo);
        String data = FreeMakerUtils.parseData(hashMap, "course_template.ftl");
        try {
            Path path = Files.createTempFile(id.toString(), ".tmp");
            File file = path.toFile();
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            InputStream inputStream = new ByteArrayInputStream(data.getBytes());
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
            mediaFeignClient.uploadFile(MultipartSupportConfig.getMultipartFile(file), "/course/" + id + ".html");
        } catch (IOException e) {
            log.error("page to static resource error id:{}", id);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addElastic(CourseIndex index) {
        return searchFeignClient.add(index);

    }
}

package cn.lomu.content;

import cn.lomu.base.model.PageParams;
import cn.lomu.base.model.PageResult;
import cn.lomu.content.config.MultipartSupportConfig;
import cn.lomu.content.feignClient.MediaFeignClient;
import cn.lomu.content.mapper.CourseBaseMapper;
import cn.lomu.content.mapper.CourseCategoryMapper;
import cn.lomu.content.mapper.TeachplanMapper;
import cn.lomu.content.model.dto.CourseCategoryTreeDTO;
import cn.lomu.content.model.dto.QueryCourseParamsDto;
import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.model.po.CourseBase;
import cn.lomu.content.service.CourseBaseService;
import cn.lomu.content.service.CourseCategoryService;
import cn.lomu.content.service.TeachplanService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author LoMu
 * Date  2023-05-02 10:22
 */

@SpringBootTest
@Slf4j
public class CourseBaseMapperTests {
    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private CourseCategoryService courseCategoryService;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private MediaFeignClient mediaFeignClient;

    @Test
    public void testCourseBaseMapper() {
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");

        PageResult<CourseBase> result = courseBaseService.queryCourseBaseList(new PageParams(1, 1), queryCourseParamsDto);
        for (CourseBase item : result.getItems()) {
            System.out.println(item.getName());
        }
    }

    @Test
    public void testCourseCategory() {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("Z:\\ServerFile\\images\\95433542.jpg"));
        mediaFeignClient.uploadFile(multipartFile, "img/");
    }


}

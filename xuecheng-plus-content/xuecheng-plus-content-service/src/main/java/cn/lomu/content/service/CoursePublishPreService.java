package cn.lomu.content.service;

import cn.lomu.content.model.po.CoursePublishPre;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author LoMu
 * Date  2023-06-07 14:08
 */
public interface CoursePublishPreService extends IService<CoursePublishPre> {
    void commitAudit(Long companyId, Long courseId);
}

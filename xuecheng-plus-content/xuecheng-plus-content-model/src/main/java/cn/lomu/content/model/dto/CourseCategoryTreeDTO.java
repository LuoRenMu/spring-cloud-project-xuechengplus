package cn.lomu.content.model.dto;

import cn.lomu.content.model.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-06 13:17
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseCategoryTreeDTO extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDTO> childrenTreeNodes;
}

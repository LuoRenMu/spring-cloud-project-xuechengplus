package cn.lomu.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-06-03 19:12
 */

@Data
public class CoursePreviewDTO {
    private List<TeachplanDTO> teachplans;

    private CourseBaseInfoDto courseBase;
}

package cn.lomu.content.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author LoMu
 * Date  2023-05-13 6:39
 */

@Data
@ToString
public class AddCourseTeacherDTO {
    private Long id;

    @NotNull
    private Long courseId;
    @NotBlank
    private String teacherName;
    @NotBlank
    private String position;
    private String introduction;
}

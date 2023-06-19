package cn.lomu.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author LoMu
 * Date  2023-05-11 3:28
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class EditCourseDTO extends AddCourseDto {

    @ApiModelProperty(value = "课程id", required = true)
    @NotNull
    private Long id;
}

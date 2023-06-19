package cn.lomu.content.model.dto;

import cn.lomu.content.model.po.Teachplan;
import cn.lomu.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-11 10:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeachplanDTO extends Teachplan {
    private TeachplanMedia teachplanMedia;

    private List<TeachplanDTO> teachPlanTreeNodes;
}

package cn.lomu.content.service;

import cn.lomu.content.model.dto.BindTeachplanMediaDTO;
import cn.lomu.content.model.dto.SaveTeachplanDTO;
import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.model.po.TeachplanMedia;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-12 2:18
 */
public interface TeachplanService {
    List<TeachplanDTO> selectTreeNodes(long courseId);

    void saveTeachplan(SaveTeachplanDTO saveTeachplanDTO);

    void deleteTeachplan(Long id);

    void moveUp(Long id);

    void moveDown(Long id);

    void associationMedia(BindTeachplanMediaDTO bindTeachplanMediaDto);

    void deleteMedia(@PathVariable long teachPlanId, @PathVariable long mediaId);
}

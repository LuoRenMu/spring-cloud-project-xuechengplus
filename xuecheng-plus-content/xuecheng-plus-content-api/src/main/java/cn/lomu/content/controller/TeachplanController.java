package cn.lomu.content.controller;

import cn.lomu.content.model.dto.BindTeachplanMediaDTO;
import cn.lomu.content.model.dto.SaveTeachplanDTO;
import cn.lomu.content.model.dto.TeachplanDTO;
import cn.lomu.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-11 10:59
 */

@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation("查询接口")
    @GetMapping("/teachplan/{id}/tree-nodes")
    public List<TeachplanDTO> getTeachplan(@PathVariable Long id) {
        return teachplanService.selectTreeNodes(id);
    }

    @ApiOperation("新增/修改计划")
    @PostMapping("/teachplan")
    public void saveTeachplanDTO(@RequestBody SaveTeachplanDTO saveTeachplanDTO) {
        teachplanService.saveTeachplan(saveTeachplanDTO);
    }

    @ApiOperation("删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable @NotNull Long id) {
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("下移")
    @PostMapping("/teachplan/movedown/{id}")
    public void moveDown(@PathVariable @NotNull Long id) {
        teachplanService.moveDown(id);
    }

    @ApiOperation("上移")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveUp(@PathVariable @NotNull Long id) {
        teachplanService.moveUp(id);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDTO bindTeachplanMediaDTO) {
        teachplanService.associationMedia(bindTeachplanMediaDTO);
    }


    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deleteMedia(@PathVariable long teachPlanId, @PathVariable long mediaId) {
        teachplanService.deleteMedia(teachPlanId, mediaId);
    }
}

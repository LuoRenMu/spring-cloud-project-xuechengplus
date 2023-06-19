package cn.lomu.content.controller;

import cn.lomu.content.model.dto.CourseCategoryTreeDTO;
import cn.lomu.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-08 15:00
 */
@RestController
@RequestMapping("/course-category")
public class CourseCategoryController {
    @Autowired
    public CourseCategoryService courseCategoryService;

    @GetMapping("/tree-nodes")
    public List<CourseCategoryTreeDTO> treeNodes() {
        return courseCategoryService.selectTreeNodes("1");
    }

}

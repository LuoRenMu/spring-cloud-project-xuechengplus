package cn.lomu.content.service;

import cn.lomu.content.model.dto.CourseCategoryTreeDTO;

import java.util.List;

/**
 * @author LoMu
 * Date  2023-05-08 14:17
 */
public interface CourseCategoryService {

    List<CourseCategoryTreeDTO> selectTreeNodes(String id);
}

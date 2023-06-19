package cn.lomu.content.service.impl;

import cn.lomu.content.mapper.CourseCategoryMapper;
import cn.lomu.content.model.dto.CourseCategoryTreeDTO;
import cn.lomu.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LoMu
 * Date  2023-05-08 14:17
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    public CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDTO> selectTreeNodes(String id) {
        List<CourseCategoryTreeDTO> rawCourseCategoryTreeDTOS = courseCategoryMapper.selectTreeNodes(id).stream()
                .filter(c -> !(Objects.equals(id, c.getId())))
                .collect(Collectors.toList());
        Map<String, CourseCategoryTreeDTO> courseCategoryTreeDTOMap = rawCourseCategoryTreeDTOS.stream()
                .collect(Collectors.toMap(CourseCategoryTreeDTO::getId, c -> c));

        List<CourseCategoryTreeDTO> categoryTreeDTOS = new ArrayList<>();
        rawCourseCategoryTreeDTOS.forEach(courseCategoryTreeDTO -> {
            CourseCategoryTreeDTO parent = courseCategoryTreeDTOMap.get(courseCategoryTreeDTO.getParentid());
            if (parent != null) {
                if (parent.getChildrenTreeNodes() != null) {
                    parent.getChildrenTreeNodes().add(courseCategoryTreeDTO);
                } else {
                    parent.setChildrenTreeNodes(new ArrayList<>());
                }
            } else {
                categoryTreeDTOS.add(courseCategoryTreeDTO);
            }
        });
        return categoryTreeDTOS;
    }
}

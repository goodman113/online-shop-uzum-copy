package project.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import project.model.SubCategory;
import project.model.SubSubCategory;
import project.model.create.SubCategoryCreateDto;
import project.model.dto.SubCategoryDto;
import project.repository.repository.CategoryRepository;
import project.repository.repository.SubCategoryRepository;
import project.repository.repository.SubSubCategoryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SubCategoryMapper {
    final CategoryRepository categoryRepository;
    final CategoryMapper categoryMapper;
    final SubSubCategoryMapper subSubCategoryMapper;
    final SubSubCategoryRepository subSubCategoryRepository;
    public SubCategory fromCreateDto(SubCategoryCreateDto dto) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(dto.getName());
        subCategory.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        return subCategory;
    }

    public SubCategoryDto toDto(SubCategory save) {
        Map<SubCategoryDto,List<SubSubCategory>> map = new HashMap<>();
        SubCategoryDto dto = new SubCategoryDto();
        dto.setId(save.getId());
        dto.setName(save.getName());
        dto.setCategoryDto(categoryMapper.toDto(save.getCategory()));

        map.put(dto,subSubCategoryRepository.findAllBySubCategory_Id(save.getId()));
        dto.setSubSubCategories(subSubCategoryMapper.toDtoList(map));
        return dto;
    }

    public List<SubCategoryDto> toDtoList(List<SubCategory> all) {
        return  all.stream().map(this::toDto).collect(Collectors.toList());
    }
}

package project.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.model.SubSubCategory;
import project.model.create.SubSubCategoryCreateDto;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.repository.repository.SubCategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubSubCategoryMapper {
    final SubCategoryRepository subCategoryRepository;
    public SubSubCategory fromCreateDto(SubSubCategoryCreateDto dto) {
        SubSubCategory subSubCategory = new SubSubCategory();
        subSubCategory.setName(dto.getName());
        subSubCategory.setSubCategory(subCategoryRepository
                .findById(dto.getSubCategoryId()).orElseThrow(()-> new RuntimeException("sub category not found")));
        subSubCategory.setSubCategory(subCategoryRepository.findById(dto.getSubCategoryId())
                .orElseThrow(()-> new RuntimeException("was not able to find SubSubCategory with id: " + dto.getSubCategoryId())));
        return subSubCategory;

    }

    public List<SubSubCategoryDto> toDtoList(Map<SubCategoryDto, List<SubSubCategory>> map) {
        List<SubSubCategoryDto> dtoList = new ArrayList<>();
        List<SubSubCategory> subSubCategories =  new ArrayList<>();
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        for (Map.Entry<SubCategoryDto, List<SubSubCategory>> entry : map.entrySet()) {
            subSubCategories = entry.getValue();
            subCategoryDto = entry.getKey();
        }
        for (SubSubCategory subSubCategory : subSubCategories) {
            Map<SubSubCategory,SubCategoryDto> subCategoryDtoMap = new HashMap<>();
            subCategoryDtoMap.put(subSubCategory,subCategoryDto);
            dtoList.add(toDto(subCategoryDtoMap));
        }
        return dtoList;
    }

    public SubSubCategoryDto toDto(Map<SubSubCategory, SubCategoryDto> dtoMap) {
        SubSubCategory subSubCategory = new SubSubCategory();
        SubCategoryDto subCategoryDto =  new SubCategoryDto();
        for (Map.Entry<SubSubCategory, SubCategoryDto> entry : dtoMap.entrySet()) {
            subSubCategory = entry.getKey();
            subCategoryDto = entry.getValue();
        }
        SubSubCategoryDto subSubCategoryDto = new SubSubCategoryDto();
        subSubCategoryDto.setId(subSubCategory.getId());
        subSubCategoryDto.setName(subSubCategory.getName());
        subSubCategoryDto.setSubCategoryDto(subCategoryDto);
        return subSubCategoryDto;
    }
}

package project.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.mapper.SubCategoryMapper;
import project.mapper.SubSubCategoryMapper;
import project.model.SubCategory;
import project.model.SubSubCategory;
import project.model.create.SubSubCategoryCreateDto;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.repository.repository.SubCategoryRepository;
import project.repository.repository.SubSubCategoryRepository;
import project.service.SubSubCategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/sub_sub_category")
@Controller
@RequiredArgsConstructor
public class SubSubCategoryController {
    final SubSubCategoryService service;
    final SubSubCategoryRepository repository;
    final SubSubCategoryMapper mapper;
    final SubCategoryRepository subCategoryRepository;
    final SubCategoryMapper  subCategoryMapper;
    final SubSubCategoryMapper  subSubCategoryMapper;

    @GetMapping
    public String subSubCatBySubCat(Model model) {
        Map<SubCategoryDto, List<SubSubCategoryDto>> subCategoryDtoListMap = new HashMap<>();
        for (SubCategoryDto subCategoryDto : subCategoryMapper.toDtoList(subCategoryRepository.findAll())) {
            Map<SubCategoryDto, List<SubSubCategory>> map = new HashMap<>();
            map.put(subCategoryDto,repository.findAllBySubCategory_IdAndDeleted(subCategoryDto.getId(),false));
            List<SubSubCategoryDto> dtoList = subSubCategoryMapper.toDtoList(map);
            subCategoryDtoListMap.put(subCategoryDto, dtoList);
        }
        model.addAttribute("sub_category", subCategoryDtoListMap);
        return "category/sub_sub_categories";
    }

    @GetMapping("/{id}")
    public String subSubCatById(@PathVariable Long id, Model model) {
        SubSubCategoryDto subSubCategoryDto = service.get(id);
        model.addAttribute("sub_category", subSubCategoryDto);
        return "category/sub_sub_category";
    }
    @PostMapping
    public String add(@RequestParam String subCategoryId, SubSubCategoryCreateDto dto) {
        dto.setSubCategoryId(Long.valueOf(subCategoryId));
        service.create(dto);
        return "redirect:/sub_category";
    }

    @DeleteMapping("/{id}")
    @Transactional
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/sub_category";
    }
}

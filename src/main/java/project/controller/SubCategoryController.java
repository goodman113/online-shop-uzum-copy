package project.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.mapper.CategoryMapper;
import project.mapper.SubCategoryMapper;
import project.model.create.SubCategoryCreateDto;
import project.model.dto.CategoryDto;
import project.model.dto.SubCategoryDto;
import project.repository.repository.CategoryRepository;
import project.repository.repository.SubCategoryRepository;
import project.service.SubCategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sub_category")
@RequiredArgsConstructor
public class SubCategoryController {
    final CategoryRepository categoryRepository;
    final SubCategoryRepository subCategoryRepository;
    final SubCategoryService service;
    final CategoryMapper categoryMapper;
    final SubCategoryMapper subCategoryMapper;
    @GetMapping
    public String allSubCatByCat(Model model) {
        Map<CategoryDto, List<SubCategoryDto>> categoryDtoListMap = new HashMap<>();
        for (CategoryDto categoryDto : categoryMapper.toDtoList(categoryRepository.findAll())) {
            List<SubCategoryDto> dtoList1 = subCategoryMapper.toDtoList
                    (subCategoryRepository.findAllByCategory_Id(categoryDto.getId()));
            categoryDtoListMap.put(categoryDto, dtoList1);
        }
        model.addAttribute("sub_and_categories", categoryDtoListMap);
        return "category/sub_categories";
    }
    @PostMapping
    public String addSubCat(@RequestParam String categoryId,
                            SubCategoryCreateDto subCategoryDto) {
        subCategoryDto.setCategoryId(Long.valueOf(categoryId));
        service.create(subCategoryDto);
        return "redirect:/sub_category";
    }
    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        SubCategoryDto subCategoryDto = service.get(id);
        model.addAttribute("sub_category", subCategoryDto);
        return "category/sub_category";
    }
    @DeleteMapping("/{id}")
    @Transactional
    public String deleteById(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/sub_category";
    }
}

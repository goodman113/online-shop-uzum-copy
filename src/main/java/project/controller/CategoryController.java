package project.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import project.model.create.CategoryCreateDto;
import project.model.dto.CategoryDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    final CategoryService service;

    @GetMapping
    public String getAll(Model model, @RequestParam (defaultValue = "", required = false) String search) {
        List<CategoryDto> categories = service.getAll(search);
        model.addAttribute("categories", categories);
        return "category/categories";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        CategoryDto categoryDto = service.get(id);
        model.addAttribute("category", categoryDto);
        return "category/category";
    }

    @PostMapping
    public String post(CategoryCreateDto categoryDto) {
        service.create(categoryDto);
        return "redirect:/category";
    }

    @DeleteMapping("/{id}")
    @Transactional
    public String delete(@PathVariable String id) {
        service.delete(Long.parseLong(id));
        return "redirect:/category";
    }
}

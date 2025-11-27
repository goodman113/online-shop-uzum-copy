package project.mapper;

import project.model.Category;
import project.model.create.CategoryCreateDto;
import project.model.dto.CategoryDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public Category fromCreateDto(CategoryCreateDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public CategoryDto toDto(Category save) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(save.getId());
        categoryDto.setName(save.getName());
        return categoryDto;
    }

    public List<CategoryDto> toDtoList(List<Category> all) {
        return  all.stream().map(this::toDto).collect(Collectors.toList());
    }
}

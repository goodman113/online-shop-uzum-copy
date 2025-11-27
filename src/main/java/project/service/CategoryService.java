package project.service;

import jakarta.persistence.EntityNotFoundException;
import project.mapper.CategoryMapper;
import project.model.Category;
import project.model.create.CategoryCreateDto;
import project.model.dto.CategoryDto;
import org.springframework.stereotype.Service;
import project.model.dto.SubCategoryDto;
import project.repository.repository.CategoryRepository;
import project.validator.CategoryValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService extends AbstractService
        <CategoryRepository, CategoryMapper, CategoryValidator>
        implements CrudService<CategoryDto, CategoryCreateDto,CategoryDto, Long> {
    final SubCategoryService subCategoryService;
    protected CategoryService(CategoryRepository repository, CategoryMapper mapper, CategoryValidator validator, SubCategoryService subCategoryService) {
        super(repository, mapper, validator);
        this.subCategoryService = subCategoryService;
    }

    @Override
    public CategoryDto create(CategoryCreateDto dto) {
        validator.onCreate(dto);
        Category category = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(category));
    }

    @Override
    public CategoryDto get(Long id) {
        Category category = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return mapper.toDto(category);

    }

    @Override
    public List<CategoryDto> getAll(String search) {
        return mapper.toDtoList(repository.findAll(search));
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        validator.onDelete(id);
        for (SubCategoryDto subCategoryDto : subCategoryService.getAll(String.valueOf(id))) {
            subCategoryService.delete(subCategoryDto.getId());
        }
        repository.deleteById(id);
    }

    public Map<CategoryDto, List<SubCategoryDto>> getAllWithSubCategories() {
        Map<CategoryDto, List<SubCategoryDto>> map = new HashMap<>();
        for (CategoryDto dto : getAll("")) {
            List<SubCategoryDto> all = subCategoryService.getAll(String.valueOf(dto.getId()));
            map.put(dto, all);
        }
        return map;
    }
}

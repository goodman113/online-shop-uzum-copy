package project.validator;

import lombok.RequiredArgsConstructor;
import project.model.create.CategoryCreateDto;
import org.springframework.stereotype.Component;
import project.repository.repository.CategoryRepository;

@Component
@RequiredArgsConstructor
public class CategoryValidator {
    final CategoryRepository repository;
    public void onCreate(CategoryCreateDto dto) {
        if (dto!=null && dto.getName() == null) {
            throw new RuntimeException("category name is required");
        }
    }

    public void onDelete(Long id) {
        boolean b = repository.existsById(id);
        if (!b) {
            throw new RuntimeException("category id not found");
        }
    }
}

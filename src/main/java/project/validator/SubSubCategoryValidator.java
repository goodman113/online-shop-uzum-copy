package project.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.model.create.SubSubCategoryCreateDto;
import project.repository.repository.SubSubCategoryRepository;

@Component
@RequiredArgsConstructor
public class SubSubCategoryValidator {
    final SubSubCategoryRepository repository;
    public void onCreate(SubSubCategoryCreateDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RuntimeException("sub category already exists with name " + dto.getName());
        }
    }

    public void onDelete(Long id) {
        boolean b = repository.existsById(id);
        if (!b) {
            throw new RuntimeException("sub category does not exist with id " + id);
        }
    }
}

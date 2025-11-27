package project.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.model.create.SubCategoryCreateDto;
import project.repository.repository.SubCategoryRepository;

@Component
@RequiredArgsConstructor
public class SubCategoryValidator {
    final SubCategoryRepository repository;
    public void onCreate(SubCategoryCreateDto dto) {
        boolean b = repository.existsByName(dto.getName());
        if (b){
            throw new RuntimeException("sub category already exists with name " + dto.getName());
        }
    }

    public void onDelete(Long id) {
        boolean b = repository.existsById(id);
        if (!b){
            throw new RuntimeException("sub category does not exist with id " + id);
        }
    }
}

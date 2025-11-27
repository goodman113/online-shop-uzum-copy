package project.validator;

import project.model.create.ProductCreateDto;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {
    public void onCreate(ProductCreateDto dto) {
        if (dto.getName()==null||dto.getName().isBlank()){
            throw new RuntimeException("name can not be empty");
        }
    }
}

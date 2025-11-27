package project.validator;

import lombok.RequiredArgsConstructor;
import project.model.create.BrandCreateDto;
import org.springframework.stereotype.Component;
import project.repository.repository.ProductRepository;

@Component
@RequiredArgsConstructor
public class BrandValidator {
    final ProductRepository productRepository;
    public void onCreate(BrandCreateDto dto) {
        if (dto == null) {
            throw new RuntimeException("brandCreateDto is null");
        }
        boolean b = productRepository.existsById(dto.getProductId());
        if (!b) {
            throw new RuntimeException("product not found");
        }
    }
}

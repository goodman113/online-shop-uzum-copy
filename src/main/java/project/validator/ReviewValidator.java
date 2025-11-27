package project.validator;

import project.model.create.ReviewCreateDto;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidator {
    public void onCreateDto(ReviewCreateDto dto) {
        if (dto.getProductId() == null) {
            throw new RuntimeException("productId is null");
        }
    }
}

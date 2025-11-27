package project.validator;

import project.model.create.OrderItemCreateDto;
import org.springframework.stereotype.Component;

@Component
public class OrderItemValidator {
    public void onCreate(OrderItemCreateDto dto) {
        if (dto.getProduct()== null) {
            throw new RuntimeException("product is null");
        }
    }
}

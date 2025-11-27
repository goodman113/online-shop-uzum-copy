package project.validator;

import project.model.create.OrderCreateDto;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {
    public void onCreate(OrderCreateDto dto) {
        if (dto.getBuyerId() == null) {
            throw new RuntimeException("buyerId is null");
        }
    }
}

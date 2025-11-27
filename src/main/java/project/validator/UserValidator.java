package project.validator;

import project.model.create.UserCreateDto;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void onCreate(UserCreateDto dto) {
        if (dto.getEmail().isBlank()) {
           throw  new RuntimeException("email is required");
        }
        if(dto.getPhone().isBlank()) {
            throw  new RuntimeException("phone is required");
        }
    }
}

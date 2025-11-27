package project.validator;

import lombok.RequiredArgsConstructor;
import project.model.create.NotificationCreateDto;
import org.springframework.stereotype.Component;
import project.repository.repository.NotificationRepository;

@Component
@RequiredArgsConstructor
public class NotificationValidator {
    final NotificationRepository repository;
    public void onCreate(NotificationCreateDto dto) {
        if (dto.getReceiverId() == null) {
            throw new RuntimeException("receiverId is null");
        }
        if (dto.getMessage().isBlank()){
            throw new RuntimeException("message is blank");
        }
    }

}

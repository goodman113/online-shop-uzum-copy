package project.model.create;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDto {
    private String message;
    private Long receiverId;
}

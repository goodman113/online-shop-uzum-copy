package project.model.create;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class NotificationCreateDto {
    private String message;
    private Long receiverId;
}

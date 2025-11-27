package project.model.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequest {
    private Long orderId;
    private String code;
}
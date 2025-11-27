package project.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PayRequest {
    private Long orderId;
    private String cardNumber;
    private String expireDate;

    // getters + setters
}
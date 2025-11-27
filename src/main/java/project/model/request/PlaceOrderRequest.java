package project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {
    private String deliveryType;
    private String paymentMethod;
    private Double deliveryPrice;
    private Double total;
    private String deliveryAddress;
}

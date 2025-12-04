package project.model.dto;

import lombok.*;
import project.model.enums.OrderStatus;
import project.model.enums.PayingMethod;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private OrderStatus orderStatus;
    private Double totalPrice;
    private UserShortDto costumer;
    private Boolean withСourier;
    private String address;
    private PayingMethod payingMethod;
    private List<OrderItemDto> orderItems;
}

package project.model.create;

import lombok.*;
import project.model.Product;
import project.model.enums.OrderItemStatus;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class OrderItemCreateDto {
    private Long id;
    private int quantity;
    private Double price;
    private Product product;
    private OrderItemStatus status;
}

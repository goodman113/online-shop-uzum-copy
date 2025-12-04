package project.model.dto;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private int quantity;
    private Double price;
    private ProductDto product;
}

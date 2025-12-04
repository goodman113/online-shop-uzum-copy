package project.model.update;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    private Long id;
    private Double price;
    private Double oldPrice;
    private int stockQuantity;
}

package project.model.create;


import lombok.*;
import project.model.Image;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ProductCreateDto {
    private String name;
    private String description;
    private Double price;
    private int stockQuantity;
    private Long categoryId;
}

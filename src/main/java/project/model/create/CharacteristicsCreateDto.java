package project.model.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.model.Product;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CharacteristicsCreateDto {
    private String name;
    private Product product;
    private Double price;
}

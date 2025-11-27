package project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicsDto {
    private Long id;
    private String name;
    private ProductDto product;
    private Double price;
}

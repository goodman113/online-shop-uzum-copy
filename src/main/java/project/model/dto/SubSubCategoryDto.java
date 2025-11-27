package project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubSubCategoryDto {
    private Long id;
    private String name;
    private SubCategoryDto subCategoryDto;
    private List<ProductDto> products = new ArrayList<>();

    @Override
    public String toString() {
        return "SubSubCategoryDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

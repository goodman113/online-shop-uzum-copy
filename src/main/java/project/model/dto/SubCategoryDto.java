package project.model.dto;


import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDto {
    private Long id;
    private String name;
    private CategoryDto categoryDto;
    private List<SubSubCategoryDto> subSubCategories;
}

package project.model.create;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubCategoryCreateDto {
    private String name;
    private Long categoryId;
}

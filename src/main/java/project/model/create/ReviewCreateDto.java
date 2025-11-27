package project.model.create;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ReviewCreateDto {
    private int rating;
    private String comment;
    private Long productId;
}

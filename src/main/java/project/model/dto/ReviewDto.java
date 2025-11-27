package project.model.dto;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private int rating;
    private String comment;
    private UserShortDto sender;
}

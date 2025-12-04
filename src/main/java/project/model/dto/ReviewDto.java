package project.model.dto;


import lombok.*;
import project.model.ImageForReview;
import project.model.ReplyToReview;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDto {
    private Long id;
    private int rating;
    private String comment;
    private UserShortDto sender;
    private List<ImageForReview> images;
    private LocalDateTime createdAt;
    private String advantages;

    private String disadvantages;
    private ReplyToReview replyToReview;
    private Boolean isReplied;
}

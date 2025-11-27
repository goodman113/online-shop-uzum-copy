package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.Review;
import project.model.create.ReviewCreateDto;
import project.model.dto.ReviewDto;
import org.springframework.stereotype.Component;
import project.model.dto.UserShortDto;
import project.repository.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    final ProductRepository productRepository;
    public Review fromCreateDto(ReviewCreateDto dto) {
        Review review = new Review();
        review.setComment(dto.getComment());
//        review.setCustomer(CurrentUser);
        review.setProduct(productRepository.findById(dto.getProductId()).orElseThrow(()-> new RuntimeException("product not found")));
        review.setRating(dto.getRating());
        return review;
    }

    public ReviewDto toDto(Review save) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(save.getId());
        reviewDto.setComment(save.getComment());
        reviewDto.setRating(save.getRating());
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setEmail(save.getCustomer().getEmail());
        userShortDto.setUsername(save.getCustomer().getUsername());
        userShortDto.setId(save.getCustomer().getId());
        reviewDto.setSender(userShortDto);
        return reviewDto;
    }

    public List<ReviewDto> toDtoList(List<Review> all) {
        return  all.stream().map(this::toDto).collect(Collectors.toList());
    }
}

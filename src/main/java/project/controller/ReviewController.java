package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.model.*;
import project.model.dto.ReviewDto;
import project.repository.repository.ProductRepository;
import project.repository.repository.ReplyToReviewRepository;
import project.repository.repository.ReviewRepository;
import project.service.ImageService;
import project.service.ReviewService;
import project.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor

public class ReviewController {
    final ReviewRepository reviewRepository;
    final ReplyToReviewRepository replyToReviewRepository;
    final ProductRepository productRepository;
    final UserService userService;
    final ImageService imageService;

    @PostMapping("/api/reviews/add")
    public ResponseEntity<Map<Object,Object>> addReview(@RequestParam("productId") Long productId,
                                                        @RequestParam("rating") int rating,
                                                        @RequestParam("comment") String comment,
                                                        @RequestParam(value = "advantages") String advantages,     // ✅ OPTIONAL
                                                        @RequestParam(value = "disadvantages") String disadvantages, // ✅ OPTIONAL
                                                        @RequestParam(value = "images", required = false) List<MultipartFile> images){

        try {
            User currentUser = userService.findCurrentUser();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Review review = new Review();
            review.setRating(rating);
            review.setComment(comment);
            review.setAdvantages(advantages != null ? advantages.trim() : null);      // ✅ SAFE
            review.setDisadvantages(disadvantages != null ? disadvantages.trim() : null); // ✅ SAFE
            review.setCustomer(currentUser);
            review.setProduct(product);


            // ✅ Images handling (unchanged - works perfectly)
            if (images != null && !images.isEmpty()) {
                List<ImageForReview> imageForReviews = new ArrayList<>();
                for (MultipartFile file : images) {
                    ImageForReview imageForReview = imageService.uploadForReview(file);
                    imageForReview.setReview(review);
                    imageForReviews.add(imageForReview);

                }
                review.setImages(imageForReviews);
            }
            reviewRepository.save(review);
            List<Review> reviews = reviewRepository.getReviewsByProduct(product);
            int size = reviews.size();
            Double sum = reviews.stream().mapToDouble(Review::getRating).sum();
            double ratingForProduct  = sum/size;

            product.setAverageRating(ratingForProduct);
            productRepository.save(product);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review added successfully"
            ));
        }catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }



    @PostMapping("/api/reviews/{reviewId}/reply")
    public ResponseEntity<Map<String, Object>> replyToReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Review not found"));
            User currentUser = userService.findCurrentUser();
            boolean isSeller = currentUser.getVendorProfile() != null &&
                    Objects.equals(review.getProduct().getVendor().getId(), currentUser.getVendorProfile().getId());

            if (!isSeller) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "У вас нет прав для ответа на этот отзыв"
                ));
            }

            String reply = request.get("reply");
            ReplyToReview replyToReview = new ReplyToReview();
            replyToReview.setReview(review);
            replyToReview.setRepliedBy(currentUser);
            replyToReview.setReply(reply);
            ReplyToReview save = replyToReviewRepository.save(replyToReview);
            review.setIsReplied(true);
            review.setReplyToReview(save);
            reviewRepository.save(review);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ответ успешно добавлен!"
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}

package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.model.Product;
import project.model.Review;
import project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import project.model.dto.ReviewDto;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> getReviewsByProduct(Product product);

    List<Review> findReviewsByCustomer(User customer);


    @Query("update Review u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Review> findReviewByIdIsAndDeleted(Long id, boolean b);

    List<Review> findReviewsByProduct(Product product);

    List<Review> getReviewsByProductId(Long productId);
}

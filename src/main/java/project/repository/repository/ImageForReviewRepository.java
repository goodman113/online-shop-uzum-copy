package project.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.ImageForReview;

public interface ImageForReviewRepository extends JpaRepository<ImageForReview, Long> {
}

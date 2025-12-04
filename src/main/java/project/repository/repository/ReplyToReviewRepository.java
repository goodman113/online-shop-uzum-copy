package project.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.ReplyToReview;

public interface ReplyToReviewRepository extends JpaRepository<ReplyToReview,Long> {
}

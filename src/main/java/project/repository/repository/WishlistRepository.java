package project.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Product;
import project.model.User;
import project.model.Wishlist;

import java.util.List;


public interface WishlistRepository extends JpaRepository<Wishlist,Long> {

    Wishlist findWishlistByUserAndProduct(User user, Product product);

    List<Wishlist> findWishlistByUser(User user);

    User user(User user);

    boolean existsByuserAndProduct(User user, Product product);
}

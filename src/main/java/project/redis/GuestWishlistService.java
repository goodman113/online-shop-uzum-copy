package project.redis;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import project.model.Product;
import project.model.User;
import project.model.Wishlist;
import project.repository.repository.ProductRepository;
import project.repository.repository.WishlistRepository;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestWishlistService {
    final StringRedisTemplate redis;
    final WishlistRepository wishlistRepository;
    final ProductRepository  productRepository;

    public void addWishlist(String wishlistId,Long productId) {
        redis.opsForHash().put("wishlist:" + wishlistId, productId.toString(), "1");
        redis.expire("wishlist:" + wishlistId, Duration.ofHours(2));
    }
    public Map<Long, String> getWishlist(String wishlistId){
        Map<Object, Object> rawMap = redis.opsForHash().entries("wishlist:" + wishlistId);
        return rawMap.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof String)
                .collect(Collectors.toMap(
                        entry -> Long.valueOf((String) entry.getKey()), // productId
                        entry -> (String) entry.getValue()
                ));
    }
    public void removeWishlist(String wishlistId,Long productId) {
        redis
                .opsForHash().delete("wishlist:"+wishlistId,productId.toString());
    }

    public void clearWishlist(String wishlistId){
        redis.delete("wishlist:" + wishlistId);
    }

    public String getOrCreateWishlistId(HttpServletRequest request,
                                        HttpServletResponse response){
        Cookie[] cookie = request.getCookies();
        if (cookie != null) {
            for (Cookie c : cookie) {
                if (c.getName().equals("wishlistId")) {
                    return c.getValue();
                }
            }
        }
        String wishlistId = UUID.randomUUID().toString();
        Cookie cookie1 = new Cookie("wishlistId", wishlistId);
        cookie1.setPath("/");
        cookie1.setMaxAge(3600);
        cookie1.setHttpOnly(true);
        cookie1.setSecure(request.isSecure());
        response.addCookie(cookie1);
        return wishlistId;
    }

    public void mergeWishlistIntoUser(String wishlistId, User user) {
        System.out.println("wishlistId:"+wishlistId);
        Map<Long, String> guestWishlist = getWishlist(wishlistId);

        for (Long productId : guestWishlist.keySet()) {
            Product product = productRepository.findById(productId)
                    .orElse(null);
            if (product != null) {
                boolean exists = wishlistRepository.existsByuserAndProduct(user, product);
                if (!exists) {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setProduct(product);
                    wishlist.setUser(user);
                    wishlistRepository.save(wishlist);
                }
            }
        }
        clearWishlist(wishlistId);
    }
}

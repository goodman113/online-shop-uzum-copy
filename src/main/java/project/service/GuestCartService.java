package project.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import project.model.User;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestCartService {
    final StringRedisTemplate redis;
    final OrderItemService orderItemService;

    public void addItem(String cartId,Long productId, int quantity){
        redis.opsForHash().increment("cart:"+cartId, productId.toString(), quantity);
        redis.expire("cart:"+cartId, Duration.ofHours(1));
    }
    public Map<Object,Object> getCart(String cartId){
        return redis.opsForHash().entries("cart:"+cartId);
    }

    public void removeItem(String cartId,Long productId){
        redis.opsForHash().delete("cart:"+cartId, productId.toString());
    }

    public void clearCart(String cartId){
        redis.opsForHash().delete("cart:"+cartId);
    }

    public String getOrCreateCartId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("cartId")) {
                    return cookie.getValue();
                }
            }
        }
        String cartId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("cartId", cartId);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);
        return cartId;

    }

    public void mergeGuestCartIntoUser(String cartId, User user) {
        System.out.println("cartId:"+cartId);
        System.out.println("userId:"+user.getId());
        Map<Object, Object> guestItems =getCart(cartId);

        for (var entry : guestItems.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer qty = Integer.valueOf(entry.getValue().toString());

            orderItemService.addToUserCart(user.getId(), productId, qty);
        }
        clearCart(cartId);
    }


    public int getCartCountFromRedis(String cookieCart) {
        Map<Object, Object> cart = getCart(cookieCart);
        int count = 0;
        for (Map.Entry<Object, Object> entry : cart.entrySet()) {
            int value = Integer.parseInt(entry.getValue().toString());
            count += value;
        }
        return count;
    }
}
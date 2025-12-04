package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import project.model.Order;
import project.model.OrderItem;
import project.model.User;
import project.model.enums.OrderStatus;
import project.model.request.AddToCartRequest;
import project.repository.repository.OrderRepository;
import project.redis.GuestCartService;
import project.service.OrderItemService;
import project.service.OrderService;
import project.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    final OrderRepository orderRepository;
    final OrderItemService orderItemService;
    final GuestCartService guestCartService;
    final UserService userService;
    final OrderService orderService;
    @GetMapping
    public ResponseEntity<Map<Object,Object>> getCart(
            HttpServletRequest request,
            HttpServletResponse httpResponse,
            Authentication authentication) {
        if (authentication!=null && authentication.isAuthenticated()){
            User user = userService.findCurrentUser();
            List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.PENDING);
            Order order = orderItemService.getOrder(user.getId(), orders);
            Map<Long, Integer> productsMap = orderItemService.getProductsAndQtyOfOrder(order);
            Map<Object,Object> map = new HashMap<>();

            productsMap.forEach((k,v)->{
                map.put(k.toString(),v);
            });
            return ResponseEntity.ok(map);
        }else {
            String cookieCart = guestCartService.getOrCreateCartId(request,httpResponse);
            Map<Object, Object> cart = guestCartService.getCart(cookieCart);
            return ResponseEntity.ok(cart);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(
            Authentication authentication,
            HttpServletResponse responseq,
            HttpServletRequest request) {
        int count = 0;
        if(authentication!=null && authentication.isAuthenticated()){
            User user = userService.findCurrentUser();
            List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.PENDING);
            Order order = orderItemService.getOrder(user.getId(), orders);
            count = order.getItems().stream().mapToInt(OrderItem::getQuantity).sum();
        }
        else {
            String cookieCart = guestCartService.getOrCreateCartId(request,responseq);
            count = guestCartService.getCartCountFromRedis(cookieCart);
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<Object,Object>> addCart(
            @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication){
        System.out.println("addCart");
        Long productId = request.getProductId();
        Integer quantity = request.getQuantity();
        if (authentication!=null && authentication.isAuthenticated()){
            User user = userService.findCurrentUser();
            List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.PENDING);
            orderItemService.putOrderItemToOrder(user.getId(), productId,quantity, orders);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "quantity", quantity,
                    "message", "Product added to your cart"
            ));
        }else {
            String cookieCart = guestCartService.getOrCreateCartId(httpRequest, httpResponse);
            guestCartService.addItem(cookieCart, productId, quantity);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "quantity", quantity,
                    "message", "Product added to cart"
            ));
        }
    }


    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication) {
        System.out.println("updateCartItem");
        Integer newQuantity = request.get("quantity");
        if (authentication!=null && authentication.isAuthenticated()){
            User user = userService.findCurrentUser();
            List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.PENDING);
            Order order = orderItemService.getOrder(user.getId(), orders);
            orderService.updateOrderItemInOrder(order,productId,newQuantity);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "quantity", newQuantity
            ));
        } else {
            // Guest user - FIXED: Don't calculate difference, just set the new quantity

            String cartId = guestCartService.getOrCreateCartId(httpRequest, httpResponse);
            // Get current quantity
            Object current = guestCartService.getCart(cartId).get(productId.toString());
            int currentQuantity = current != null ? Integer.parseInt(current.toString()) : 0;

            // Calculate difference to add/subtract
            int difference = newQuantity - currentQuantity;

            // Add the difference (can be negative to decrease)
            guestCartService.addItem(cartId, productId, difference);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "quantity", newQuantity
            ));
        }
    }

    /**
     * DELETE /api/cart/remove/{productId}
     * Remove product from cart
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Long productId,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication){
        System.out.println("removeCartItem");
        if (authentication!=null && authentication.isAuthenticated()){
            System.out.println("from db");
            User user = userService.findCurrentUser();
            orderItemService.removeItemFromOrder(user,productId);
        }else {
            System.out.println("from redis");
            String cookieCart = guestCartService.getOrCreateCartId(httpRequest, httpResponse);
            guestCartService.removeItem(cookieCart,productId);
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Product removed from cart"
        ));
    }

    /**
     * POST /api/cart/merge
     * Merge guest cart to user cart on login
     */

    @PostMapping("/merge")
    public ResponseEntity<?> mergeGuestCart(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication) {

        if (authentication!=null && authentication.isAuthenticated()){
            String cookieCart = guestCartService.getOrCreateCartId(httpRequest, httpResponse);
            User user = userService.findCurrentUser();
            guestCartService.mergeGuestCartIntoUser(cookieCart, user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cart merged successfully"
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "User not authenticated"
        ));
    }
}

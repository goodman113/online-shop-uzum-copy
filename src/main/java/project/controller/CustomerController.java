package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.mapper.OrderMapper;
import project.mapper.ProductMapper;
import project.model.*;
import project.model.dto.*;
import project.model.enums.OrderItemStatus;
import project.model.enums.OrderStatus;
import project.model.enums.PayingMethod;
import project.model.request.PayRequest;
import project.model.request.PlaceOrderRequest;
import project.model.request.ResendRequest;
import project.model.request.VerifyRequest;
import project.model.response.PaymentResponse;
import project.repository.repository.*;
import project.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping()
public class CustomerController {
    final ProductService productService;
    final ProductRepository productRepository;
    final ProductMapper productMapper;
    final GuestCartService  guestCartService;
    final CategoryService  categoryService;
    private final UserService userService;
    final OrderItemService orderItemService;
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final OrderService orderService;
    final UserRepository userRepository;
    final VendorRepository vendorRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @GetMapping
    public String dashboard(Model model,
                            @RequestParam(name = "name",required = false, defaultValue = "") String name,
                            @RequestParam(name = "description" ,required = false, defaultValue = "")String description,
                            @RequestParam(name = "vendorId", required = false)Long vendorId,
                            @RequestParam(name = "categoryName",required = false, defaultValue = "")String categoryName,
                            @RequestParam(name = "priceFrom", required = false)Double priceFrom,
                            @RequestParam(name = "priceTo",required = false)Double priceTo, HttpServletRequest request,
                            HttpServletResponse response) {
        List<ProductDto> all;
        if (name.isBlank() && description.isBlank() && categoryName.isBlank()
                && priceFrom==null && priceTo==null&&vendorId==null) {
            all = productMapper.toDtoList(productRepository.findProductsByAverageRatingAsc());
        }
        else
            all = productService.getAll(name, description,vendorId, categoryName,priceFrom, priceTo);
        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();
        model.addAttribute("categories", allWithSubCategories);
        model.addAttribute("products", all);
        return "customer/dashboard";
    }

    @GetMapping("/cart")
    public String cart(Model model,
                       HttpServletRequest request,
                       HttpServletResponse httpResponse,
                       Authentication authentication) {

        OrderDto order = new OrderDto();
        User user = null;
        String cookieCart = "";

        if (authentication != null && authentication.isAuthenticated()) {
            user = userService.findCurrentUser();
            List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.PENDING);
            order = orderMapper.toDto(orderItemService.getOrder(user.getId(), orders));
            System.out.println("order: "+order);
            order.getOrderItems().forEach(System.out::println);
        } else {
            cookieCart = guestCartService.getOrCreateCartId(request, httpResponse);
            Map<Object, Object> cart = guestCartService.getCart(cookieCart);

            List<OrderItemDto> items = new ArrayList<>();
            double totalPrice = 0;

            for (var entry : cart.entrySet()) {
                Long productId = Long.valueOf(entry.getKey().toString());
                Integer qty = Integer.valueOf(entry.getValue().toString());

                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) continue;

                OrderItemDto item = new OrderItemDto();
                item.setProduct(productMapper.toDto(product));
                item.setQuantity(qty);
                item.setPrice(product.getPrice());

                items.add(item);
                totalPrice += product.getPrice() * qty;
            }

            order.setOrderItems(items);
            order.setTotalPrice(totalPrice);
        }

        // FIXED CART COUNT LOGIC
        Integer cartCount;
        if (user != null && order.getId() != null) {
            Integer count = orderItemRepository.getAllQuantity(order.getId());
            cartCount = (count != null) ? count : 0;
        } else {
            cartCount = guestCartService.getCartCountFromRedis(cookieCart);
        }

        model.addAttribute("cartItemCount", cartCount);
        model.addAttribute("vendors", vendorRepository.findAll());
        model.addAttribute("order", order);

        return "customer/cart";
    }

    @GetMapping("/checkout/{orderId}")
    public String checkout(Model model,HttpServletRequest request,@PathVariable("orderId") Long orderId) {
        User user = userService.findCurrentUser();
        OrderDto order = orderService.get(orderId);
        int count = order.getOrderItems().stream().mapToInt(OrderItemDto::getQuantity).sum();
        double total = order.getOrderItems().stream().mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity()).sum();

        System.out.println("total = " + total);
        model.addAttribute("itemCount", count);
        model.addAttribute("itemsTotal",total);
        model.addAttribute("totalAmount", total);
        model.addAttribute("discount",15);
        model.addAttribute("deliveryPrice", 30);
        model.addAttribute("user", user);
        return "customer/checkout";
    }

    @GetMapping("/product/{id}")
    public String productInfo(@PathVariable("id") String id, Model model,HttpServletRequest request) {
        ProductDto productDto = productService.get(Long.parseLong(id));
        model.addAttribute("product", productDto);
        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        return "customer/productInfo";
    }

    @PostMapping("/api/orders/place")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request) {
        User user = userService.findCurrentUser();

        List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.PENDING);
        Order order = orderItemService.getOrder(user.getId(), orders);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "No active order"));
        }

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < item.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false,
                                "message", "Stock too low for " + product.getName()));
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            product.setSoldQuantity(product.getSoldQuantity() + item.getQuantity());
            item.setStatus(OrderItemStatus.PENDING);
            productRepository.save(product);
        }

        order.setTotalPrice(request.getTotal());
        order.setAddress(request.getDeliveryAddress() == null ? "" : request.getDeliveryAddress());
        order.setPayingMethod(request.getPaymentMethod().equals("card") ? PayingMethod.CARD : PayingMethod.CASH);
        order.setWithСourier(request.getDeliveryType().equals("courier"));
        order.setStatus(OrderStatus.UNPAID);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", order.getId()
        ));
    }

    @GetMapping("/orders")
    public String orders(
            @RequestParam String filter,
            Model model,
            HttpServletRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findCurrentUser();
        List<Order> orders;
        List<Review> reviews = reviewRepository.findReviewsByCustomer(user);
        if (filter.equals("all")){
            orders = orderRepository.findOrdersByCustomer(user);
        }else if (filter.equals("unpaid")){
            orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.UNPAID);
        }
        else {
            orders = orderRepository.findOrdersByCustomerAndAnyStatus(user);
        }
        model.addAttribute("filter", filter);
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("orders", orders);

        return "customer/orders";
    }


    @PostMapping("/api/orders/pay")
    @ResponseBody
    public ResponseEntity<PaymentResponse> pay(@RequestBody PayRequest payRequest){
        try {
            Order order = orderRepository.findById(payRequest.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));

            if (order == null) {
                return ResponseEntity.ok(new PaymentResponse(false, "Заказ не найден"));
            }

            if (order.getStatus() != OrderStatus.UNPAID) {
                return ResponseEntity.ok(new PaymentResponse(false, "Заказ уже оплачен или отменён"));
            }

            if (order.getPayingMethod() != PayingMethod.CARD) {
                return ResponseEntity.ok(new PaymentResponse(false, "Этот заказ нельзя оплатить картой"));
            }

                order.setStatus(OrderStatus.ACCEPTED);
                orderRepository.save(order);



                return ResponseEntity.ok(new PaymentResponse(true, "code was sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.ok(new PaymentResponse(false, "Произошла ошибка: " + e.getMessage()));
        }
    }

    @PostMapping("/api/orders/verify")
    public ResponseEntity<PaymentResponse> verifyCode(@RequestBody VerifyRequest request) {
        // Check if code is valid and not expired
        // If valid, process payment and update order status
        return ResponseEntity.ok(new PaymentResponse(true/false, "message"));
    }

    // 3. Resend code
    @PostMapping("/api/orders/resend-code")
    public ResponseEntity<PaymentResponse> resendCode(@RequestBody ResendRequest request) {
        // Generate new code
        // Send new code
        return ResponseEntity.ok(new PaymentResponse(true, "Код отправлен"));
    }

}


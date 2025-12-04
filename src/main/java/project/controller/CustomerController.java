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
import project.email.MailService;
import project.mapper.OrderMapper;
import project.mapper.ProductMapper;
import project.mapper.ReviewMapper;
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
import project.redis.GuestCartService;
import project.redis.GuestWishlistService;
import project.repository.repository.*;
import project.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping()
public class CustomerController {
    final ProductService productService;
    final ProductRepository productRepository;
    final ProductMapper productMapper;
    final GuestCartService guestCartService;
    final CategoryService  categoryService;
    private final UserService userService;
    final OrderItemService orderItemService;
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final OrderService orderService;
    final WishlistRepository wishlistRepository;
    final GuestWishlistService guestWishlistService;
    final VendorRepository vendorRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    final PaymentVerificationRepository paymentVerificationRepository;
    final MailService mailService;
    final ReviewMapper reviewMapper;
    private final SubCategoryRepository subCategoryRepository;
    private final SubSubCategoryRepository subSubCategoryRepository;

    @GetMapping
    public String dashboard(Model model,
                            @RequestParam(name = "name",required = false, defaultValue = "") String name,
                            @RequestParam(name = "description" ,required = false, defaultValue = "")String description,
                            @RequestParam(name = "vendorId", required = false)Long vendorId,
                            @RequestParam(name = "categoryName",required = false, defaultValue = "")String categoryName,
                            @RequestParam(name = "subCategoryName",required = false,defaultValue = "") String subCategoryName,
                            @RequestParam(name = "subSubCategoryName",required = false,defaultValue = "") String subSubCategoryName,
                            @RequestParam(name = "priceFrom", required = false)Double priceFrom,
                            @RequestParam(name = "priceTo",required = false)Double priceTo, HttpServletRequest request,
                            HttpServletResponse response, Authentication authentication) {
        List<ProductDto> all = new ArrayList<>();

        if (name.isBlank() && description.isBlank() && categoryName.isBlank() && subCategoryName.isBlank() && subSubCategoryName.isBlank()
                && priceFrom==null && priceTo==null&&vendorId==null) {
            all = productMapper.toDtoList(productRepository.findProductsByAverageRatingAsc());
        } else if (!categoryName.isBlank() &&subCategoryName.isBlank() && subSubCategoryName.isBlank()) {
            List<SubCategory> subCategories = subCategoryRepository.findAllByCategory_Name(categoryName);
            for (SubCategory subCategory : subCategories) {
                List<SubSubCategory> subSubCategories = subSubCategoryRepository.findAllBySubCategory_Id(subCategory.getId());
                for (SubSubCategory subSubCategory : subSubCategories) {
                    all.addAll(productMapper.toDtoList(productRepository.findProductsByCategory(subSubCategory)));
                }
            }
        } else if (categoryName.isBlank() &&!subCategoryName.isBlank() && subSubCategoryName.isBlank()) {
            List<SubSubCategory> subSubCategories = subSubCategoryRepository.findAllBySubCategoryName(subCategoryName);
            for (SubSubCategory subSubCategory : subSubCategories) {
                all.addAll(productMapper.toDtoList(productRepository.findProductsByCategory(subSubCategory)));
            }
        } else
            all = productService.getAll(name, description,vendorId, subSubCategoryName,priceFrom, priceTo);

        List<Product> productsWishlist;
        if   (authentication==null){
            String wishlistId = guestWishlistService.getOrCreateWishlistId(request, response);
            Map<Long, String> guestWishlist = guestWishlistService.getWishlist(wishlistId);

            productsWishlist = productRepository.findAllById(guestWishlist.keySet());

        }
        else {
            User currentUser = userService.findCurrentUser();
            List<Wishlist> wishlistByUser = wishlistRepository.findWishlistByUser(currentUser);
            productsWishlist = wishlistByUser.stream().map(Wishlist::getProduct).toList();

        }
        Set<Long> wishlistProductIds = productsWishlist.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());



        List<ProductDto> filteredWishlistProducts = all.stream()
                .filter(product -> wishlistProductIds.contains(product.getId()))
                .toList();
        model.addAttribute("wishlistCount", productsWishlist.size());
        System.out.println("productWishlistCount: " + productsWishlist.size());
            model.addAttribute("productsWishlist",productsWishlist);
            model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        model.addAttribute("productsWishlist", filteredWishlistProducts); // ✅ Only matching products
        model.addAttribute("wishlistProductIds", wishlistProductIds);
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
        int cartCount;
        if (user != null && order.getId() != null) {
            Integer count = orderItemRepository.getAllQuantity(order.getId());
            cartCount = (count != null) ? count : 0;
        } else {
            cartCount = guestCartService.getCartCountFromRedis(cookieCart);
        }
        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();
        model.addAttribute("categories", allWithSubCategories);
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
        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();
        model.addAttribute("categories", allWithSubCategories);
        model.addAttribute("itemCount", count);
        model.addAttribute("itemsTotal",total);
        model.addAttribute("totalAmount", total);
        model.addAttribute("discount",15);
        model.addAttribute("deliveryPrice", 30);
        model.addAttribute("user", user);
        return "customer/checkout";
    }

    @GetMapping("/product/{id}")
    public String productInfo(@PathVariable("id") String id,
                              Authentication authentication,Model model,HttpServletRequest request) {
        ProductDto productDto = productService.get(Long.parseLong(id));
        LocalDateTime date = LocalDateTime.now().minusDays(7);
        List<OrderItem> items = orderItemRepository.findOrderItemsByStatusAndUpdatedAt(OrderItemStatus.PURCHASED, date);
        int weeklySales = items.size();
        System.out.println("weeklySales = " + weeklySales);
        boolean isSeller= false;
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findCurrentUser();
            if (user.getVendorProfile()!=null) {
                isSeller = Objects.equals(productDto.getVendorId(), user.getVendorProfile().getId());
            }
        }
        List<ReviewDto> reviews = reviewMapper.toDtoList(reviewRepository.getReviewsByProductId(Long.parseLong(id)));
        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();
        model.addAttribute("categories", allWithSubCategories);
        model.addAttribute("reviews", reviews);
        model.addAttribute("isSeller", isSeller);
        model.addAttribute("weeklySales", weeklySales);
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
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String reviewFilter,
            @RequestParam(required = false) String activeSection,
            Model model, Authentication authentication
    ) {
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findCurrentUser();
        List<Order> orders;
        if (filter != null && filter.equals("all")){
            orders = orderRepository.findOrdersByCustomer(user);
        } else if (filter != null && filter.equals("unpaid")){
            orders = orderRepository.findOrdersByCustomerAndStatus(user, OrderStatus.UNPAID);
        } else {
            orders = orderRepository.findOrdersByCustomerAndAnyStatus(user);
        }

        // 🔥 REVIEWS LOGIC
        List<Review> allReviews = reviewRepository.findReviewsByCustomer(user);
        List<Review> filteredReviews = allReviews;

        if (reviewFilter != null) {
            if (reviewFilter.equals("replied")) {
                filteredReviews = allReviews.stream()
                        .filter(review -> review.getReplyToReview() != null)
                        .collect(Collectors.toList());
            } else if (reviewFilter.equals("unreplied")) {
                filteredReviews = allReviews.stream()
                        .filter(review -> review.getReplyToReview() == null)
                        .collect(Collectors.toList());
            }
        }

        List<Long> reviewedProductIds = allReviews.stream()
                .map(review -> review.getProduct().getId())
                .collect(Collectors.toList());

        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();

        // ✅ PASS ALL DATA
        model.addAttribute("categories", allWithSubCategories);
        model.addAttribute("reviewedProductIds", reviewedProductIds);
        model.addAttribute("filter", filter);
        model.addAttribute("reviewFilter", reviewFilter);
        model.addAttribute("activeSection", activeSection);
        model.addAttribute("reviews", allReviews);
        model.addAttribute("filteredReviews", filteredReviews);
        model.addAttribute("user", user);
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

            sendCodeToEmail(order);


            return ResponseEntity.ok(new PaymentResponse(true, "code was sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.ok(new PaymentResponse(false, "Произошла ошибка: " + e.getMessage()));
        }
    }

    @PostMapping("/api/orders/verify")
    public ResponseEntity<PaymentResponse> verifyCode(@RequestBody VerifyRequest request) {
        User user = userService.findCurrentUser();
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
        Optional<PaymentVerificationCode> verificationCode = paymentVerificationRepository.findByEmailAndOrderIdAndUsedFalse(user.getEmail(), request.getOrderId());
        PaymentVerificationCode code = verificationCode.get();
        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            return  ResponseEntity.ok(new PaymentResponse(false, "code expired"));
        }
        if (code.isUsed()){
            return ResponseEntity.ok(new PaymentResponse(true, "code was already used"));
        }
        if (!code.getCode().equals(request.getCode())) {
            return ResponseEntity.ok(new PaymentResponse(false, "code is wrong"));
        }

        order.setStatus(OrderStatus.ACCEPTED);
        List<OrderItem> items = order.getItems();
        for (OrderItem item : items) {
            item.setStatus(OrderItemStatus.PURCHASED);
            orderItemRepository.save(item);
        }
        orderRepository.save(order);
        return ResponseEntity.ok(new PaymentResponse(true, "order was successfully verified"));
    }

    // 3. Resend code
    @PostMapping("/api/orders/resend-code")
    public ResponseEntity<PaymentResponse> resendCode(@RequestBody ResendRequest request) {
        // Generate new code
        // Send new code
        User user = userService.findCurrentUser();
        paymentVerificationRepository.deleteAll(paymentVerificationRepository.findPaymentVerificationCodesByEmail(user.getEmail()));

        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
        sendCodeToEmail(order);


        return ResponseEntity.ok(new PaymentResponse(true, "Код отправлен"));
    }

    private void sendCodeToEmail(Order order) {
        User user = userService.findCurrentUser();
        PaymentVerificationCode code = new PaymentVerificationCode();
        code.setOrderId(order.getId());
        code.setCode(generateCode());
        code.setEmail(user.getEmail());
        code.setExpiresAt(LocalDateTime.now().plusMinutes(1));
        code.setUsed(false);
        paymentVerificationRepository.save(code);

        mailService.sendMail(user.getEmail(), code.getCode());
    }

    public String generateCode() {
        return String.valueOf((int)(Math.random() * 9000 + 1000));
    }


}


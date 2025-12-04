package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.mapper.ProductMapper;
import project.model.Product;
import project.model.User;
import project.model.Wishlist;
import project.model.dto.CategoryDto;
import project.model.dto.ProductDto;
import project.model.dto.SubCategoryDto;
import project.redis.GuestWishlistService;
import project.repository.repository.ProductRepository;
import project.repository.repository.WishlistRepository;
import project.service.CategoryService;
import project.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping
@Controller
@RequiredArgsConstructor
public class WishlistController {
    final WishlistRepository wishlistRepository;
    final ProductRepository productRepository;
    final UserService userService;
    final GuestWishlistService  guestWishlistService;
    final ProductMapper  productMapper;
    final CategoryService categoryService;

    @GetMapping("/wishlist")
    public String wishlist(Model model, HttpServletRequest request,
                           HttpServletResponse response,
                           Authentication authentication) {
        List<ProductDto> productDtos = new ArrayList<>();
        if (authentication == null) {
            String wishlistId = guestWishlistService.getOrCreateWishlistId(request, response);
            Map<Long, String> guestWishlist = guestWishlistService.getWishlist(wishlistId);
            productDtos = productMapper.toDtoList(guestWishlist.keySet().stream()
                    .map(productRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList()));
        }
        else {
            User user = userService.findCurrentUser();
            List<Wishlist> wishlistByUser = wishlistRepository.findWishlistByUser(user);

            for (Wishlist wishlist : wishlistByUser) {
                productDtos.add(productMapper.toDto(wishlist.getProduct()));
            }
        }
        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();
        model.addAttribute("categories", allWithSubCategories);
        model.addAttribute("products", productDtos);
        return "wishlist/wishlist";
    }

    @ResponseBody
    @PostMapping("/api/wishlist/add/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(@PathVariable Long productId,
                                                             Authentication authentication,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        if (authentication==null){
            String id = guestWishlistService.getOrCreateWishlistId(request, response);
            guestWishlistService.addWishlist(id,productId);


        }
        else {
            User user = userService.findCurrentUser();
            Wishlist wishlist = new Wishlist();
            wishlist.setProduct(productRepository.findById(productId).orElseThrow());
            wishlist.setUser(user);
            wishlistRepository.save(wishlist);
        }
        try {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product added to wishlist"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


    @ResponseBody
    @DeleteMapping("/api/wishlist/remove/{productId}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable Long productId,
                                                      Authentication authentication,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        try {
            if  (authentication==null){
                String id = guestWishlistService.getOrCreateWishlistId(request, response);
                guestWishlistService.removeWishlist(id,productId);

            }
            else {
                User user = userService.findCurrentUser();
                Product product = productRepository.findById(productId).orElseThrow();
                wishlistRepository.delete(wishlistRepository.findWishlistByUserAndProduct(user, product));
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product removed from wishlist"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @ResponseBody
    @GetMapping("/api/wishlist")
    public ResponseEntity<List<Product>> getWishlist(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<Product> products;
        try {
            if   (authentication==null){
                String wishlistId = guestWishlistService.getOrCreateWishlistId(request, response);
                Map<Long, String> guestWishlist = guestWishlistService.getWishlist(wishlistId);
                products = guestWishlist.keySet().stream()
                        .map(productRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            }
            else {
            User currentUser = userService.findCurrentUser();
            List<Wishlist> wishlists = wishlistRepository.findWishlistByUser(currentUser);
            products =wishlists.stream()
                    .map(Wishlist::getProduct)
                    .collect(Collectors.toList());
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}

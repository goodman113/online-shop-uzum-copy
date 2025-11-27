package project.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import project.config.SecurityConfig;
import project.model.Product;
import project.model.SubCategory;
import project.model.SubSubCategory;
import project.model.User;
import project.model.create.ProductCreateDto;
import project.model.dto.ProductDto;
import org.springframework.stereotype.Component;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.repository.repository.*;
import project.service.ReviewService;
import project.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    final SubSubCategoryRepository categoryRepository;
    final SubSubCategoryMapper  subSubCategoryMapper;
    final SubCategoryMapper  subCategoryMapper;
    final SubCategoryRepository subCategoryRepository;
    final ReviewService reviewService;
    final ReviewMapper  reviewMapper;
    final ImageRepository imageRepository;
    final UserRepository userRepository;
    public Product fromCreateDto(ProductCreateDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setSoldQuantity(0L);
        product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("category not found")));
        product.setDescription(dto.getDescription());
        product.setStockQuantity(dto.getStockQuantity());
        product.setVendor(findCurrentUser().getVendorProfile());
        return product;
    }

    public ProductDto toDto(Product save) {
        Map<SubSubCategory, SubCategoryDto> dtoMap = new HashMap<>();
        ProductDto  productDto = new ProductDto();
        productDto.setId(save.getId());
        productDto.setName(save.getName());
        productDto.setPrice(save.getPrice());
        productDto.setOldPrice(save.getOldPrice());
        productDto.setAverageRating(save.getAverageRating());
        SubCategory subCategory = subCategoryRepository.findById(save
                .getCategory().getSubCategory()
                .getId()).orElseThrow(() -> new RuntimeException("category not found"));
        dtoMap.put(save.getCategory(), subCategoryMapper.toDto(subCategory));
        productDto.setSubSubCategoryDto(subSubCategoryMapper.toDto(dtoMap));
        productDto.setDescription(save.getDescription());
        productDto.setStockQuantity(save.getStockQuantity());
        productDto.setVendorId(save.getVendor().getId());
        productDto.setSoldQuantity(save.getSoldQuantity());
        productDto.setImageProducts(imageRepository.findImagesByProduct_Id(save.getId()));
        productDto.setReviews(reviewMapper.toDtoList(reviewService.getAllReviewForProduct(save)));
        return productDto;
    }

    public List<ProductDto> toDtoList(List<Product> all) {
        return all.stream().map(this::toDto).collect(Collectors.toList());
    }
    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user1 = (UserDetails) auth.getPrincipal();
        return userRepository.findByUsername(user1.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}

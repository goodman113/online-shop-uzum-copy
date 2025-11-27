package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.config.SecurityConfig;
import project.config.VendorUpdateWebSocketHandler;
import project.mapper.*;
import project.model.*;
import project.model.create.ProductCreateDto;
import project.model.dto.IdNameDto;
import project.model.dto.ProductDto;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.repository.repository.*;
import project.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/vendor")
@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
@RequiredArgsConstructor
public class VendorController {
    final ProductRepository productRepository;
    final UserService userService;
    final ProductMapper productMapper;
    final CategoryService categoryService;
    final SubSubCategoryRepository subSubCategoryRepository;
    final SubSubCategoryMapper subSubCategoryMapper;
    final SubCategoryRepository subCategoryRepository;
    final SubCategoryMapper subCategoryMapper;
    final UserMapper userMapper;
    final ImageService imageService;



    @GetMapping("/profile")
    public String vendor(Model model,HttpServletRequest request) {
        User user = userService.findCurrentUser();
        model.addAttribute("userDto", userMapper.toDto(user,null,null));
        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        List<Product> productsByVendorId = productRepository.findProductsByVendor_Id(user.getVendorProfile().getId());
        long sum = productsByVendorId.stream().mapToLong(Product::getSoldQuantity).sum();
        model.addAttribute("totalSold",sum);
        return "vendor/vendorInfo";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id,Model model,HttpServletRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("product was not found"));
        model.addAttribute("product", productMapper.toDto(product));
        System.out.println(product);
        System.out.println("productMapper -->"+ productMapper.toDto(product));
        model.addAttribute("currentWebPage", GlobalControllerAdvice.populateCurrentPage(request));
        return "vendor/productInfo";
    }


    @GetMapping("/products")
    public String products(
                           @RequestParam(defaultValue = "0") int page, Model model, HttpServletRequest  request) {
        User user = userService.findCurrentUser();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("soldQuantity").descending());
        Page<Product> products = productRepository.findProductsByVendor_Id(user.getVendorProfile().getId() ,pageable);
        Double totalPrice = products.stream().mapToDouble(p ->p.getPrice()*p.getSoldQuantity()).sum();
        Long lowStock = products.stream().filter(p ->p.getSoldQuantity()<=10).count();
        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalRevenue", totalPrice);
        model.addAttribute("lowStockCount", lowStock);
        model.addAttribute("products", productMapper.toDtoList(products.getContent()));
        model.addAttribute("categories", categoryService.getAll(""));

        return "vendor/products";
    }

    @PostMapping(value ="/product/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String add(ProductCreateDto product,
                      @RequestParam("image") List<MultipartFile> files,
                      RedirectAttributes ra) {
        List<Image> images =  new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                images.add(imageService.upload(file));
            }
        }
        Product product1 = productMapper.fromCreateDto(product);

        for (Image img : images) {
            img.setProduct(product1);
        }
        productRepository.save(product1);

        ra.addFlashAttribute("success", "Product added successfully!");
        return "redirect:/vendor/products";
    }

    @GetMapping("/api/subcategories/{categoryId}")
    @ResponseBody
    public List<IdNameDto> getSubCategories(@PathVariable Long categoryId) {
        return subCategoryRepository
                .findSubCategoriesByCategory_Id(categoryId)
                .stream()
                .map(sc -> new IdNameDto(sc.getId(), sc.getName()))
                .toList();
    }

    @GetMapping("/api/subsubcategories/{subCategoryId}")
    @ResponseBody
    public List<IdNameDto> getSubSubCategories(@PathVariable Long subCategoryId) {
        return subSubCategoryRepository
                .findAllBySubCategory_Id(subCategoryId)
                .stream()
                .map(ssc -> new IdNameDto(ssc.getId(), ssc.getName()))
                .toList();
    }

}

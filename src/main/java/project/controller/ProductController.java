package project.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import project.model.create.ProductCreateDto;
import project.model.dto.ProductDto;
import project.model.update.ProductUpdateDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/product")
@PreAuthorize("hasAnyAuthority('ADMIN', 'VENDOR')")
@RequiredArgsConstructor
public class ProductController {
    final ProductService service;

    @GetMapping
    public String product(Model model,
                          @RequestParam(name = "name",required = false, defaultValue = "") String name,
                          @RequestParam(name = "description" ,required = false, defaultValue = "")String description,
                          @RequestParam(name = "vendorId", required = false,defaultValue = "")Long vendorId,
                          @RequestParam(name = "categoryName",required = false, defaultValue = "")String categoryName,
                          @RequestParam(name = "priceFrom", required = false)Double priceFrom,
                          @RequestParam(name = "priceTo",required = false)Double priceTo) {
        List<ProductDto> all = service.getAll(name, description,vendorId, categoryName,priceFrom, priceTo);
        model.addAttribute("products", all);
        return "product/products";
    }

    @PostMapping
    public String product(ProductCreateDto product) {
        service.create(product);
        return "redirect:/product";
    }
    @PutMapping("/{id}")
    public String product(ProductUpdateDto product, @PathVariable String id) {
        product.setId(Long.parseLong(id));
        service.update(product);
        return "redirect:/product";
    }
    @DeleteMapping("/{id}")
    @Transactional
    public String product(@PathVariable String id) {
        service.delete(Long.parseLong(id));
        return "redirect:/product";
    }
}

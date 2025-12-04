package project.model.dto;


import lombok.*;
import project.model.Image;
import project.model.SubSubCategory;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private int stockQuantity;
    private Long vendorId;
    private List<Image> imageProducts;
    private Double oldPrice;
    private Double averageRating;
    private SubSubCategoryDto subSubCategoryDto;
    private Long soldQuantity;
    private List<ReviewDto> reviews;

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", vendorId=" + vendorId +
                ", imageProducts=" + imageProducts +
                ", oldPrice=" + oldPrice +
                ", averageRating=" + averageRating +
                ", soldQuantity=" + soldQuantity +
                ", reviews=" + reviews +
                '}';
    }
}

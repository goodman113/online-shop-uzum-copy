package project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import project.model.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SubSubCategory category;

    @ManyToOne
    private Brand brand;

    private Long soldQuantity;

    @Column(name = "old_price")
    private Double oldPrice;

    @Column(name = "average_rating")
    private Double averageRating;


    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Image> imageProducts = new ArrayList<>();
    // Getters and Setters
}

package project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import project.model.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendor")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor extends BaseEntity {

    @Column(name = "shop_name")
    private String shopName;

    private String description;

    private boolean approved = false;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    // Getters and Setters
}

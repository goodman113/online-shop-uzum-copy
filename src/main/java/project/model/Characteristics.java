package project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.model.base.BaseEntity;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Characteristics extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String name;
    private Double price;
}

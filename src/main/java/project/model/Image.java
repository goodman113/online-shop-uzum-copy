package project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import project.model.base.BaseEntity;

@Setter
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image extends BaseEntity {
    private String name;
    @Column(name = "original_name")
    private String originalName;
    private String path;
    @Column(name = "content_type")
    private String contentType;
    private Long size;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}

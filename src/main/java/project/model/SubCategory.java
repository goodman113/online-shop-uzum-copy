package project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.model.base.BaseEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubCategory extends BaseEntity {
    private String name;
    @ManyToOne
    @JsonIgnore
    private Category category;
}

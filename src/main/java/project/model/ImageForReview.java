package project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
public class ImageForReview extends BaseEntity {
    private String name;
    @Column(name = "original_name")
    private String originalName;
    private String path;
    @Column(name = "content_type")
    private String contentType;
    private Long size;

    @ManyToOne
    @JoinColumn(name = "review_id")
    @JsonIgnore
    private Review review;
}

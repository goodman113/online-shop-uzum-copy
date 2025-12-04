package project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.model.base.BaseEntity;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyToReview extends BaseEntity {

    @Column(nullable = false)
    private String reply;

    @OneToOne(optional = false)
    private Review review;

    @ManyToOne(optional = false)
    private User repliedBy;
}


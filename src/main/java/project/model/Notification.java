package project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.model.base.BaseEntity;

@Entity
@Table(name = "notification")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    private String message;
    private boolean read = false;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // Getters and Setters
}

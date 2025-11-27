package project.model;

import jakarta.persistence.*;
import lombok.*;
import project.model.base.BaseEntity;
import project.model.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private boolean enabled = true;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role; // CUSTOMER, VENDOR, ADMIN

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    // For vendors only
    @OneToOne
    private Vendor vendorProfile;

    // Getters and Setters
}

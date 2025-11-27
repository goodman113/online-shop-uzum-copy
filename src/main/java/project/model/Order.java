package project.model;

import jakarta.persistence.*;
import lombok.*;
import project.model.base.BaseEntity;
import project.model.enums.OrderStatus;
import project.model.enums.PayingMethod;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    @Column(name = "total_price")
    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private PayingMethod payingMethod;
    @Column(name = "with_courier")
    private Boolean withСourier;
    private String address;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}

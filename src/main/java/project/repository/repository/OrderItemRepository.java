package project.repository.repository;

import project.model.OrderItem;
import project.model.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("select o from OrderItem o where o.status = :status ")
    List<OrderItem> findAllByStatus(OrderItemStatus status);

    Optional<OrderItem> findOrderItemById(Long id);

    @Query("select sum(o.quantity) from OrderItem o where o.order.id = :id")
    Integer getAllQuantity(Long id);
}

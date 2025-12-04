package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import project.model.Order;
import project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("""
    select n
    from Order n
    where n.createdAt >= COALESCE(:from, n.createdAt)
      and n.createdAt <= COALESCE(:to, n.createdAt)
      and n.deleted = false
      and n.status != 'PENDING'
""")
    List<Order> findAll(@Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

    List<Order> findOrdersByCustomer(User customer);


    @Query("update Order u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Order> findUserByIdIsAndDeleted(Long id, boolean b);

    List<Order> findOrdersByCustomerAndStatus(User customer, OrderStatus status);

    @Query("select o from Order o where o.status='ACCEPTED' or o.status='PREPARING' or o.status='READY_FOR_PICKUP'")
    List<Order> findOrdersByCustomerAndAnyStatus(User user);

    List<Order> findOrdersByStatus(OrderStatus status);

    @Modifying
    @Query("""
    update Order o
    set o.status = :newStatus
    where o.status = :currentStatus
      and o.updatedAt <= :cutoff
""")
    int updateStatus(@Param("currentStatus") OrderStatus currentStatus,
                     @Param("newStatus") OrderStatus newStatus,
                     @Param("cutoff") LocalDateTime cutoff);
}

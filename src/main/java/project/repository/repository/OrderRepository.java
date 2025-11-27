package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
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
    where (:from is null or n.createdAt >= :from)
      and (:to is null or n.createdAt <= :to)
      and n.deleted =false
""")
    List<Order> findAll(String search, LocalDateTime from, LocalDateTime to);

    List<Order> findOrdersByCustomer(User customer);


    @Query("update Order u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Order> findUserByIdIsAndDeleted(Long id, boolean b);

    List<Order> findOrdersByCustomerAndStatus(User customer, OrderStatus status);

    @Query("select o from Order o where o.status='ACCEPTED' or o.status='PREPARING' or o.status='READY_FOR_PICKUP'")
    List<Order> findOrdersByCustomerAndAnyStatus(User user);
}

package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
import project.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("""
    select n 
    from Notification n 
    where (:search is null or lower(n.message) like lower(concat('%', :search, '%')))
      and (:from is null or n.createdAt >= :from)
      and (:to is null or n.createdAt <= :to)
      and n.deleted= false
""")
    List<Notification> findAll(
            @Param("search") String search,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );


    @Query("update Notification u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Notification> findUserByIdIsAndDeleted(Long id, boolean b);
}

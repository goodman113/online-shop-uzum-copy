package project.repository.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor,Long> {
    List<Vendor> getAllByApproved(boolean approved);

    long countVendorsByApproved(boolean approved);

    @Query("SELECT v FROM Vendor v ORDER BY v.updatedAt DESC LIMIT 5")
    List<Vendor> findTop5ByOrderByUpdatedAtDesc();

    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.createdAt >= :weekAgo GROUP BY FUNCTION('DATE', v.createdAt) ORDER BY FUNCTION('DATE', v.createdAt)")
    List<Long> countNewVendorsLast7Days(@Param("weekAgo") LocalDateTime weekAgo);


    @Query("update Vendor u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Vendor> findUserByIdIsAndDeleted(Long id, boolean b);
}

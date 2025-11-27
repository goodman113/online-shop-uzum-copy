package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Long> {

    @Query("update Brand u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Brand> findUserByIdIsAndDeleted(Long id, boolean b);

    List<Brand> findAllByDeleted(Boolean deleted);
}

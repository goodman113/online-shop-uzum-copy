package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
import project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("select c from Category c where lower(c.name) like lower(concat('%', :search, '%')) " +
            "and c.deleted=false")
    List<Category> findAll(String search);


    @Query("update Category u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);
}

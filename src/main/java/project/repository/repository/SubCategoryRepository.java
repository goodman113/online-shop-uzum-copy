package project.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.model.SubCategory;
import project.model.dto.SubCategoryDto;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {
    boolean existsByName(String name);

    List<SubCategory> findAllByCategory_Id(Long categoryId);

    List<SubCategory> findSubCategoriesByCategory_Id(Long categoryId);


    @Query("update SubCategory u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);


    Optional<SubCategory> findSubCategoryByIdIsAndDeleted(Long id, boolean b);

    List<SubCategory> findAllByCategory_Name(String categoryName);
}

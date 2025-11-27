package project.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.model.SubCategory;
import project.model.SubSubCategory;

import java.util.List;
import java.util.Optional;

public interface SubSubCategoryRepository extends JpaRepository<SubSubCategory,Long> {

    boolean existsByName(String name);

    List<SubSubCategory> findAllBySubCategory_Id(Long subCategoryId);


    @Query("update SubSubCategory u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<SubSubCategory> findUserByIdIsAndDeleted(Long id, Boolean deleted);

    List<SubSubCategory> findAllBySubCategory_IdAndDeleted(Long subCategoryId, Boolean deleted);

    Optional<SubSubCategory> findSubSubCategoryByIdIsAndDeleted(Long id, boolean b);
}

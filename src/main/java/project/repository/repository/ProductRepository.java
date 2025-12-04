package project.repository.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import project.model.Product;
import project.model.SubSubCategory;
import project.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.model.dto.ProductDto;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("""
SELECT p
FROM Product p
JOIN p.category c
WHERE (:name IS NULL OR p.name ILIKE CONCAT('%', :name, '%'))
  AND (:description IS NULL OR p.description ILIKE CONCAT('%', :description, '%'))
  AND (:vendorId IS NULL OR p.vendor.id = :vendorId)
  AND (:categoryName IS NULL OR c.name ILIKE CONCAT('%', :categoryName, '%'))
  AND (:priceFrom IS NULL OR p.price >= :priceFrom)
  AND (:priceTo IS NULL OR p.price <= :priceTo)
""")
    List<Product> findAll(
            @Param("name") String name,
            @Param("description") String description,
            @Param("vendorId") Long vendorId,
            @Param("categoryName") String categoryName,
            @Param("priceFrom") Double priceFrom,
            @Param("priceTo") Double priceTo
    );

    @Query("select p from Product p where p.vendor = :vendorProfile")
    List<Product> findProductsByVendor(Vendor vendorProfile);

    @Query("select p from Product p")
    List<Product> findAllProductsByBrandId(Long id);

    void deleteAllByCategory_Id(Long categoryId);

    Page<Product> findProductsByVendor_Id(Long vendorId, Pageable pageable);

    List<Product> findProductsByVendor_Id(Long id);


    @Query("select p from Product p order by p.averageRating asc")
    List<Product> findProductsByAverageRatingAsc();


    @Query("update Product u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Product> findProductByIdIsAndDeleted(Long id, boolean b);

    List<Product> findProductsByCategory(SubSubCategory category);
}

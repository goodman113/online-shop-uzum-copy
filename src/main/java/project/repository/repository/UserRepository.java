package project.repository.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.model.Vendor;
import project.model.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query("select u from User u where lower(u.username) like lower(concat('%', :search, '%'))")
    List<User> findAll(String search);

    Optional<User> findByUsername(String username);

    Optional<User> findByVendorProfile_Id(Long vendorProfileId);

    List<User> findAllByVendorProfile_Approved(boolean vendorProfileApproved);

    @Query("select count (u) from User u where u.role= :role")
    long countUsersByRoleCustomer(Role role);

    long countUsersByRole(Role role);

    Page<User> findUsersByVendorProfile_Approved(boolean vendorProfileApproved, Pageable pageable);

    Page<User> findByVendorProfile_ShopNameContainingIgnoreCase(String search, Pageable pageable);

    Optional<User> findUserByIdIsAndDeleted(Long id, Boolean deleted);

    @Query("update User u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);
}

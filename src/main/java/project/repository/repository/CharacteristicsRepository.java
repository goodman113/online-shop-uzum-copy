package project.repository.repository;

import org.springframework.data.jpa.repository.Modifying;
import project.model.Characteristics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CharacteristicsRepository extends JpaRepository<Characteristics,Long> {
    boolean existsByName(String name);

    @Query("select c from Characteristics c where lower(c.name) like lower(concat('%', :search, '%'))")
    List<Characteristics> findAll(String search);


    @Query("update Characteristics u  set u.deleted=true where u.id= :id")
    @Modifying(clearAutomatically = true)
    void deleteById(Long id);

    Optional<Characteristics> findUserByIdIsAndDeleted(Long id, boolean b);
}

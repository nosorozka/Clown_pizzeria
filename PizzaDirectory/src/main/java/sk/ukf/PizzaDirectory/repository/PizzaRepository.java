package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.ukf.PizzaDirectory.entity.Pizza;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    
    // Find by slug
    Optional<Pizza> findBySlug(String slug);
    
    Optional<Pizza> findBySlugAndDeletedFalse(String slug);
    
    // Find with deleted filter
    List<Pizza> findByDeletedFalse();
    
    List<Pizza> findByNameContainingIgnoreCaseAndDeletedFalse(String name);
    
    @Query("SELECT DISTINCT p FROM Pizza p LEFT JOIN FETCH p.ingredients LEFT JOIN FETCH p.sizes LEFT JOIN FETCH p.tags WHERE p.id = :id AND p.deleted = false")
    Optional<Pizza> findByIdWithDetails(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT p FROM Pizza p LEFT JOIN FETCH p.tags WHERE p.deleted = false")
    List<Pizza> findAllWithTags();
    
    @Query("SELECT DISTINCT p FROM Pizza p JOIN p.tags t WHERE t.id = :tagId AND p.deleted = false")
    List<Pizza> findByTagId(@Param("tagId") Integer tagId);
    
    // Legacy methods (keep for admin to see all)
    List<Pizza> findByNameContainingIgnoreCase(String name);
}


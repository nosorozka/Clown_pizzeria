package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.ukf.PizzaDirectory.entity.Pizza;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    
    List<Pizza> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT DISTINCT p FROM Pizza p LEFT JOIN FETCH p.ingredients LEFT JOIN FETCH p.sizes LEFT JOIN FETCH p.tags WHERE p.id = :id")
    Optional<Pizza> findByIdWithDetails(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT p FROM Pizza p LEFT JOIN FETCH p.tags")
    List<Pizza> findAllWithTags();
    
    @Query("SELECT DISTINCT p FROM Pizza p JOIN p.tags t WHERE t.id = :tagId")
    List<Pizza> findByTagId(@Param("tagId") Integer tagId);
}


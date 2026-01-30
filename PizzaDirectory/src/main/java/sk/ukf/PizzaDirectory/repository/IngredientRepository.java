package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.ukf.PizzaDirectory.entity.Ingredient;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    
    // Methods with soft delete filter
    List<Ingredient> findByDeletedFalse();
    
    List<Ingredient> findByNameContainingIgnoreCaseAndDeletedFalse(String name);
    
    // Legacy methods (keep for admin to see all)
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}


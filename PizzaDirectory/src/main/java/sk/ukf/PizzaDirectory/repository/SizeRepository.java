package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.ukf.PizzaDirectory.entity.PizzaSize;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<PizzaSize, Integer> {
    Optional<PizzaSize> findByName(String name);
}


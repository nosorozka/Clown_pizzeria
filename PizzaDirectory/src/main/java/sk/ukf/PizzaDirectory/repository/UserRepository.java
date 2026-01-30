package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.ukf.PizzaDirectory.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndDeletedFalse(String email);
    
    boolean existsByEmail(String email);
    
    // Methods with soft delete filter
    List<User> findByDeletedFalse();
    
    Page<User> findByDeletedFalse(Pageable pageable);
}


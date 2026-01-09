package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.ukf.PizzaDirectory.entity.Order;
import sk.ukf.PizzaDirectory.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    
    List<Order> findByStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);
    
    List<Order> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.pizza LEFT JOIN FETCH oi.size WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Integer id);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Integer id);
}

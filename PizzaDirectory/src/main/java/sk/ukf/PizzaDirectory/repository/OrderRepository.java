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
    
    // Methods for cook and courier assignment
    List<Order> findByCookIdOrderByCreatedAtDesc(Integer cookId);
    
    List<Order> findByCourierIdOrderByCreatedAtDesc(Integer courierId);
    
    List<Order> findByCookIsNullAndStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);
    
    List<Order> findByCourierIsNullAndStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE (o.cook IS NULL OR o.cook.id = :userId) AND o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findUnassignedOrAssignedToCook(@Param("userId") Integer userId, @Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE (o.courier IS NULL OR o.courier.id = :userId) AND o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findUnassignedOrAssignedToCourier(@Param("userId") Integer userId, @Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.pizza LEFT JOIN FETCH oi.size WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Integer id);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Integer id);
}

package sk.ukf.PizzaDirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.ukf.PizzaDirectory.entity.OrderItem;
import sk.ukf.PizzaDirectory.entity.OrderItemId;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByOrderId(Integer orderId);
}


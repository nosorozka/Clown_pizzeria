package sk.ukf.PizzaDirectory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ukf.PizzaDirectory.dto.CartItem;
import sk.ukf.PizzaDirectory.entity.*;
import sk.ukf.PizzaDirectory.exception.ResourceNotFoundException;
import sk.ukf.PizzaDirectory.repository.OrderRepository;
import sk.ukf.PizzaDirectory.repository.PizzaRepository;
import sk.ukf.PizzaDirectory.repository.SizeRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final SizeRepository sizeRepository;

    public OrderService(OrderRepository orderRepository, PizzaRepository pizzaRepository, SizeRepository sizeRepository) {
        this.orderRepository = orderRepository;
        this.pizzaRepository = pizzaRepository;
        this.sizeRepository = sizeRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Order findById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional(readOnly = true)
    public Order findByIdWithItems(Integer id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional(readOnly = true)
    public List<Order> findByUserId(Integer userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public List<Order> findByStatuses(List<OrderStatus> statuses) {
        return orderRepository.findByStatusInOrderByCreatedAtDesc(statuses);
    }

    // For cook: PENDING and COOKING orders
    @Transactional(readOnly = true)
    public List<Order> findOrdersForCook() {
        return findByStatuses(Arrays.asList(OrderStatus.PENDING, OrderStatus.COOKING));
    }

    // For courier: READY and DELIVERING orders
    @Transactional(readOnly = true)
    public List<Order> findOrdersForCourier() {
        return findByStatuses(Arrays.asList(OrderStatus.READY, OrderStatus.DELIVERING));
    }

    public Order createOrder(User user, List<CartItem> cartItems, String address) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            Pizza pizza = pizzaRepository.findById(item.getPizzaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pizza", item.getPizzaId()));
            
            Size size = null;
            if (item.getSizeId() != null) {
                size = sizeRepository.findById(item.getSizeId()).orElse(null);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPizza(pizza);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSize(size);
            orderItem.setUnitPrice(item.getUnitPrice());

            order.addOrderItem(orderItem);
            total = total.add(item.getSubtotal());
        }

        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    public Order updateStatus(Integer id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteById(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", id);
        }
        orderRepository.deleteById(id);
    }
}

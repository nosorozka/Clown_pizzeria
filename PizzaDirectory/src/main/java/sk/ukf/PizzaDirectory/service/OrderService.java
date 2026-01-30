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

    // For cook: show unassigned or assigned to this cook
    @Transactional(readOnly = true)
    public List<Order> findOrdersForCook(Integer cookId) {
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.PENDING, OrderStatus.COOKING);
        return orderRepository.findUnassignedOrAssignedToCook(cookId, statuses);
    }

    // For courier: show unassigned or assigned to this courier
    @Transactional(readOnly = true)
    public List<Order> findOrdersForCourier(Integer courierId) {
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.READY, OrderStatus.DELIVERING);
        return orderRepository.findUnassignedOrAssignedToCourier(courierId, statuses);
    }

    /** Orders completed by this cook (assigned to cook, any status). */
    @Transactional(readOnly = true)
    public List<Order> findCompletedByCook(Integer cookId) {
        return orderRepository.findByCookIdOrderByCreatedAtDesc(cookId);
    }

    /** Orders completed by this courier (assigned to courier, any status). */
    @Transactional(readOnly = true)
    public List<Order> findCompletedByCourier(Integer courierId) {
        return orderRepository.findByCourierIdOrderByCreatedAtDesc(courierId);
    }

    // Legacy methods (keep for backward compatibility)
    @Transactional(readOnly = true)
    public List<Order> findOrdersForCook() {
        return findByStatuses(Arrays.asList(OrderStatus.PENDING, OrderStatus.COOKING));
    }

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
            
            PizzaSize size = null;
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

    public Order cancelOrder(Integer id, Integer userId) {
        Order order = findById(id);

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // Update status with automatic assignment
    public Order updateStatusWithAssignment(Integer orderId, OrderStatus newStatus, Integer userId) {
        Order order = findById(orderId);
        OrderStatus oldStatus = order.getStatus();
        
        // Assign cook when transitioning to COOKING
        if (newStatus == OrderStatus.COOKING && order.getCook() == null) {
            User cook = new User();
            cook.setId(userId);
            order.assignCook(cook);
        }
        
        // Assign courier when transitioning to DELIVERING
        if (newStatus == OrderStatus.DELIVERING && order.getCourier() == null) {
            User courier = new User();
            courier.setId(userId);
            order.assignCourier(courier);
        }
        
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /** Cook can change order status only if they are the customer OR current status is not DELIVERING/DELIVERED. */
    public boolean canCookChangeStatus(Order order, User cook) {
        if (order.getUser() != null && order.getUser().getId().equals(cook.getId())) {
            return true;
        }
        OrderStatus s = order.getStatus();
        return s != OrderStatus.DELIVERING && s != OrderStatus.DELIVERED;
    }

    // Check if status can be rolled back
    public boolean canRollbackStatus(Order order, User user, String roleName) {
        OrderStatus currentStatus = order.getStatus();
        
        // Admin can rollback any status
        if ("ROLE_ADMIN".equals(roleName)) {
            return true;
        }
        
        // Cook can rollback COOKING -> PENDING only for own orders
        if ("ROLE_COOK".equals(roleName)) {
            return currentStatus == OrderStatus.COOKING && 
                   order.getCook() != null && 
                   order.getCook().getId().equals(user.getId());
        }
        
        // Courier can rollback DELIVERING -> READY only for own orders
        if ("ROLE_COURIER".equals(roleName)) {
            return currentStatus == OrderStatus.DELIVERING && 
                   order.getCourier() != null && 
                   order.getCourier().getId().equals(user.getId());
        }
        
        return false;
    }

    // Get previous status for rollback
    public OrderStatus getPreviousStatus(OrderStatus currentStatus) {
        switch (currentStatus) {
            case COOKING:
                return OrderStatus.PENDING;
            case READY:
                return OrderStatus.COOKING;
            case DELIVERING:
                return OrderStatus.READY;
            case DELIVERED:
                return OrderStatus.DELIVERING;
            default:
                return currentStatus;
        }
    }
}

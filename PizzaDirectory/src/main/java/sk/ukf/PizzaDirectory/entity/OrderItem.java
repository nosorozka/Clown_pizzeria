package sk.ukf.PizzaDirectory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
public class OrderItem {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    private Order order;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;

    @NotNull(message = "Množstvo je povinné")
    @Min(value = 1, message = "Množstvo musí byť aspoň 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Store size info at order time
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    private Size size;

    // Store price at order time to preserve historical pricing
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    public OrderItem() {
    }

    public OrderItem(Order order, Pizza pizza, Integer quantity) {
        this.order = order;
        this.pizza = pizza;
        this.quantity = quantity;
    }

    public OrderItem(Order order, Pizza pizza, Integer quantity, Size size, BigDecimal unitPrice) {
        this.order = order;
        this.pizza = pizza;
        this.quantity = quantity;
        this.size = size;
        this.unitPrice = unitPrice;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}

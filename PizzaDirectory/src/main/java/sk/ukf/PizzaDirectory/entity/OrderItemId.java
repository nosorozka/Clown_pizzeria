package sk.ukf.PizzaDirectory.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for OrderItem entity
 */
public class OrderItemId implements Serializable {

    private Integer order;
    private Integer pizza;

    public OrderItemId() {
    }

    public OrderItemId(Integer order, Integer pizza) {
        this.order = order;
        this.pizza = pizza;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getPizza() {
        return pizza;
    }

    public void setPizza(Integer pizza) {
        this.pizza = pizza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(order, that.order) && Objects.equals(pizza, that.pizza);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, pizza);
    }
}


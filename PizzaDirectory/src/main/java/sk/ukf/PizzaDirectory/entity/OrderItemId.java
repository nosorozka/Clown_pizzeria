package sk.ukf.PizzaDirectory.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for OrderItem entity: order + pizza + size
 */
public class OrderItemId implements Serializable {

    private Integer order;
    private Integer pizza;
    private Integer size;

    public OrderItemId() {
    }

    public OrderItemId(Integer order, Integer pizza, Integer size) {
        this.order = order;
        this.pizza = pizza;
        this.size = size;
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(order, that.order) 
                && Objects.equals(pizza, that.pizza)
                && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, pizza, size);
    }
}


package sk.ukf.PizzaDirectory.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for cart items stored in session
 */
public class CartItem {

    private Integer pizzaId;
    private String pizzaName;
    private Integer sizeId;
    private String sizeName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String imagePath;

    public CartItem() {
    }

    public CartItem(Integer pizzaId, String pizzaName, Integer sizeId, String sizeName, 
                    Integer quantity, BigDecimal unitPrice, String imagePath) {
        this.pizzaId = pizzaId;
        this.pizzaName = pizzaName;
        this.sizeId = sizeId;
        this.sizeName = sizeName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.imagePath = imagePath;
    }

    public Integer getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(Integer pizzaId) {
        this.pizzaId = pizzaId;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }

    public Integer getSizeId() {
        return sizeId;
    }

    public void setSizeId(Integer sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BigDecimal getSubtotal() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Unique key for identifying item in cart (pizza + size combination)
     */
    public String getCartKey() {
        return pizzaId + "-" + sizeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(pizzaId, cartItem.pizzaId) && Objects.equals(sizeId, cartItem.sizeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pizzaId, sizeId);
    }
}


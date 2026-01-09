package sk.ukf.PizzaDirectory.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import sk.ukf.PizzaDirectory.dto.CartItem;
import sk.ukf.PizzaDirectory.entity.Pizza;
import sk.ukf.PizzaDirectory.entity.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";

    private final PizzaService pizzaService;
    private final SizeService sizeService;

    public CartService(PizzaService pizzaService, SizeService sizeService) {
        this.pizzaService = pizzaService;
        this.sizeService = sizeService;
    }

    /**
     * Get cart items from session
     */
    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    /**
     * Add item to cart
     */
    public void addToCart(HttpSession session, Integer pizzaId, Integer sizeId, Integer quantity) {
        List<CartItem> cart = getCart(session);
        
        Pizza pizza = pizzaService.findById(pizzaId);
        Size size = sizeService.findById(sizeId);
        
        // Calculate price: base pizza price + size surcharge
        BigDecimal unitPrice = pizza.getPrice().add(size.getPrice());
        
        // Check if item already in cart
        String cartKey = pizzaId + "-" + sizeId;
        for (CartItem item : cart) {
            if (item.getCartKey().equals(cartKey)) {
                item.setQuantity(item.getQuantity() + quantity);
                session.setAttribute(CART_SESSION_KEY, cart);
                return;
            }
        }
        
        // Add new item
        CartItem newItem = new CartItem(
                pizzaId,
                pizza.getName(),
                sizeId,
                size.getName(),
                quantity,
                unitPrice,
                pizza.getImagePath()
        );
        cart.add(newItem);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * Update item quantity
     */
    public void updateQuantity(HttpSession session, Integer pizzaId, Integer sizeId, Integer quantity) {
        List<CartItem> cart = getCart(session);
        String cartKey = pizzaId + "-" + sizeId;
        
        for (CartItem item : cart) {
            if (item.getCartKey().equals(cartKey)) {
                if (quantity <= 0) {
                    cart.remove(item);
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(HttpSession session, Integer pizzaId, Integer sizeId) {
        List<CartItem> cart = getCart(session);
        String cartKey = pizzaId + "-" + sizeId;
        cart.removeIf(item -> item.getCartKey().equals(cartKey));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * Clear entire cart
     */
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    /**
     * Get cart total
     */
    public BigDecimal getCartTotal(HttpSession session) {
        List<CartItem> cart = getCart(session);
        return cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get cart item count
     */
    public int getCartItemCount(HttpSession session) {
        List<CartItem> cart = getCart(session);
        return cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}


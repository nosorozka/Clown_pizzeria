package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.dto.CartItem;
import sk.ukf.PizzaDirectory.entity.Order;
import sk.ukf.PizzaDirectory.entity.User;
import sk.ukf.PizzaDirectory.service.CartService;
import sk.ukf.PizzaDirectory.service.OrderService;
import sk.ukf.PizzaDirectory.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String orderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Order> orders = orderService.findByUserId(user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "order/history";
    }

    @GetMapping("/{id}")
    public String orderDetail(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            HttpSession session
    ) {
        Order order = orderService.findByIdWithItems(id);
        
        // Check if order belongs to current user (unless admin)
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders";
        }
        
        model.addAttribute("order", order);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "order/detail";
    }

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        List<CartItem> cartItems = cartService.getCart(session);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String address,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        List<CartItem> cartItems = cartService.getCart(session);
        
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Košík je prázdny");
            return "redirect:/cart";
        }
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user can make orders (only ROLE_USER)
        if (!userService.canMakeOrders(user)) {
            redirectAttributes.addFlashAttribute("error", "Nemáte oprávnenie vytvárať objednávky");
            return "redirect:/";
        }
        
        // Update user address if provided
        if (address != null && !address.isBlank()) {
            userService.updateAddress(user, address);
        }
        
        Order order = orderService.createOrder(user, cartItems, address);
        cartService.clearCart(session);
        
        redirectAttributes.addFlashAttribute("success", "Objednávka úspešne odoslaná! Objednávka #" + order.getId());
        return "redirect:/orders/" + order.getId();
    }

    @PostMapping("/{id}/cancel")
    public String canceOrder(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Pouzivatel bol nenajdeny"));

        try {
            orderService.cancelOrder(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Objednavka bola zrusena");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Tuto objednavku nie je mozna zrusit");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Nemate opravemie zrusit tuto objednavku");
        }

        return "redirect:/orders/" + id;
    }
}

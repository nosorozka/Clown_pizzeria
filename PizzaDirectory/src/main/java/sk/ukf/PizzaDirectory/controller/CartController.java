package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.ukf.PizzaDirectory.service.CartService;

import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "cart/view";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(
            @RequestParam Integer pizzaId,
            @RequestParam Integer sizeId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session
    ) {
        cartService.addToCart(session, pizzaId, sizeId, quantity);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "cartCount", cartService.getCartItemCount(session),
                "cartTotal", cartService.getCartTotal(session)
        ));
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(
            @RequestParam Integer pizzaId,
            @RequestParam Integer sizeId,
            @RequestParam Integer quantity,
            HttpSession session
    ) {
        cartService.updateQuantity(session, pizzaId, sizeId, quantity);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "cartCount", cartService.getCartItemCount(session),
                "cartTotal", cartService.getCartTotal(session)
        ));
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(
            @RequestParam Integer pizzaId,
            @RequestParam Integer sizeId,
            HttpSession session
    ) {
        cartService.removeFromCart(session, pizzaId, sizeId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "cartCount", cartService.getCartItemCount(session),
                "cartTotal", cartService.getCartTotal(session)
        ));
    }

    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<?> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok(Map.of("success", true, "cartCount", 0));
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<?> getCartCount(HttpSession session) {
        return ResponseEntity.ok(Map.of(
                "cartCount", cartService.getCartItemCount(session),
                "cartTotal", cartService.getCartTotal(session)
        ));
    }
}


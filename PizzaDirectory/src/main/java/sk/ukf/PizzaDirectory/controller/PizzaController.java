package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.ukf.PizzaDirectory.entity.Pizza;
import sk.ukf.PizzaDirectory.service.CartService;
import sk.ukf.PizzaDirectory.service.PizzaService;
import sk.ukf.PizzaDirectory.service.SizeService;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    private final PizzaService pizzaService;
    private final SizeService sizeService;
    private final CartService cartService;

    public PizzaController(PizzaService pizzaService, SizeService sizeService, CartService cartService) {
        this.pizzaService = pizzaService;
        this.sizeService = sizeService;
        this.cartService = cartService;
    }

    /**
     * Public pizza detail is canonical by SLUG only.
     * (No id-or-slug guessing to avoid ambiguous URLs and edge cases like slug="123".)
     */
    @GetMapping("/{slug}")
    public String pizzaDetail(@PathVariable String slug, Model model, HttpSession session) {
        Pizza pizza = pizzaService.findBySlug(slug);

        model.addAttribute("pizza", pizza);
        model.addAttribute("sizes", sizeService.findAll());
        model.addAttribute("cartCount", cartService.getCartItemCount(session));

        return "pizza/detail";
    }
}

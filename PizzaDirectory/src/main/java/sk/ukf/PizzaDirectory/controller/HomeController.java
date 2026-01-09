package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sk.ukf.PizzaDirectory.entity.Pizza;
import sk.ukf.PizzaDirectory.service.CartService;
import sk.ukf.PizzaDirectory.service.PizzaService;
import sk.ukf.PizzaDirectory.service.TagService;

import java.util.List;

@Controller
public class HomeController {

    private final PizzaService pizzaService;
    private final TagService tagService;
    private final CartService cartService;

    public HomeController(PizzaService pizzaService, TagService tagService, CartService cartService) {
        this.pizzaService = pizzaService;
        this.tagService = tagService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        return menu(null, null, model, session);
    }

    @GetMapping("/menu")
    public String menu(
            @RequestParam(required = false) Integer tagId,
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session
    ) {
        List<Pizza> pizzas;
        
        if (tagId != null) {
            pizzas = pizzaService.findByTagId(tagId);
        } else if (search != null && !search.isBlank()) {
            pizzas = pizzaService.searchByName(search);
        } else {
            pizzas = pizzaService.findAllWithTags();
        }

        model.addAttribute("pizzas", pizzas);
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("selectedTagId", tagId);
        model.addAttribute("search", search);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "index";
    }
}


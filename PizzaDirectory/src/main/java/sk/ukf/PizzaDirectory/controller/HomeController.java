package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private static final int PAGE_SIZE = 8; 

    public HomeController(PizzaService pizzaService, TagService tagService, CartService cartService) {
        this.pizzaService = pizzaService;
        this.tagService = tagService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page, 
                      Model model, HttpSession session) {
        return menu(null, null, page, model, session);
    }

    @GetMapping("/menu")
    public String menu(
            @RequestParam(required = false) Integer tagId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session
    ) {
        List<Pizza> pizzas;
        int totalPages = 1;
        long totalItems = 0;
        
        // If filtering by tag or search, show all results (no pagination for filtered results)
        if (tagId != null) {
            pizzas = pizzaService.findByTagId(tagId);
            totalItems = pizzas.size();
        } else if (search != null && !search.isBlank()) {
            pizzas = pizzaService.searchByName(search);
            totalItems = pizzas.size();
        } else {
            // Use pagination for main listing
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            Page<Pizza> pizzaPage = pizzaService.findAllWithTags(pageable);
            pizzas = pizzaPage.getContent();
            totalPages = pizzaPage.getTotalPages();
            totalItems = pizzaPage.getTotalElements();
        }

        model.addAttribute("pizzas", pizzas);
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("selectedTagId", tagId);
        model.addAttribute("search", search);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        
        return "index";
    }
}


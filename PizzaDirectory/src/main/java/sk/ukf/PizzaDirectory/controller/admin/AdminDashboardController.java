package sk.ukf.PizzaDirectory.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.ukf.PizzaDirectory.service.*;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final PizzaService pizzaService;
    private final OrderService orderService;
    private final UserService userService;
    private final IngredientService ingredientService;

    public AdminDashboardController(PizzaService pizzaService, OrderService orderService,
                                     UserService userService, IngredientService ingredientService) {
        this.pizzaService = pizzaService;
        this.orderService = orderService;
        this.userService = userService;
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pizzaCount", pizzaService.findAll().size());
        model.addAttribute("orderCount", orderService.findAll().size());
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("ingredientCount", ingredientService.findAll().size());
        model.addAttribute("recentOrders", orderService.findAll().stream().limit(5).toList());
        return "admin/dashboard";
    }
}


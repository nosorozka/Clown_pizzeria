package sk.ukf.PizzaDirectory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.Order;
import sk.ukf.PizzaDirectory.entity.OrderStatus;
import sk.ukf.PizzaDirectory.service.OrderService;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for cook dashboard - manages order preparation
 */
@Controller
@RequestMapping("/cook")
public class CookController {

    private final OrderService orderService;

    // Statuses available for cook
    private static final List<OrderStatus> COOK_STATUSES = Arrays.asList(
            OrderStatus.COOKING,
            OrderStatus.READY
    );

    public CookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<Order> orders = orderService.findOrdersForCook();
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", COOK_STATUSES);
        return "cook/dashboard";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Integer id, Model model) {
        Order order = orderService.findByIdWithItems(id);
        model.addAttribute("order", order);
        model.addAttribute("statuses", COOK_STATUSES);
        return "cook/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Integer id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        // Validate that cook can only set COOKING or READY
        if (!COOK_STATUSES.contains(status)) {
            redirectAttributes.addFlashAttribute("error", "Neplatný stav pre kuchára");
            return "redirect:/cook";
        }
        
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Stav objednávky aktualizovaný");
        return "redirect:/cook";
    }
}


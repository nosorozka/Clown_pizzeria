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
 * Controller for courier dashboard - manages order delivery
 */
@Controller
@RequestMapping("/courier")
public class CourierController {

    private final OrderService orderService;

    // Statuses available for courier
    private static final List<OrderStatus> COURIER_STATUSES = Arrays.asList(
            OrderStatus.DELIVERING,
            OrderStatus.DELIVERED
    );

    public CourierController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<Order> orders = orderService.findOrdersForCourier();
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", COURIER_STATUSES);
        return "courier/dashboard";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Integer id, Model model) {
        Order order = orderService.findByIdWithItems(id);
        model.addAttribute("order", order);
        model.addAttribute("statuses", COURIER_STATUSES);
        return "courier/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Integer id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        // Validate that courier can only set DELIVERING or DELIVERED
        if (!COURIER_STATUSES.contains(status)) {
            redirectAttributes.addFlashAttribute("error", "Neplatný stav pre kuriéra");
            return "redirect:/courier";
        }
        
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Stav objednávky aktualizovaný");
        return "redirect:/courier";
    }
}


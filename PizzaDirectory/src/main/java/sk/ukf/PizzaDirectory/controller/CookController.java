package sk.ukf.PizzaDirectory.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.Order;
import sk.ukf.PizzaDirectory.entity.OrderStatus;
import sk.ukf.PizzaDirectory.entity.User;
import sk.ukf.PizzaDirectory.service.OrderService;
import sk.ukf.PizzaDirectory.service.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for cook dashboard - manages order preparation
 */
@Controller
@RequestMapping("/cook")
public class CookController {

    private final OrderService orderService;
    private final UserService userService;

    // Statuses available for cook
    private static final List<OrderStatus> COOK_STATUSES = Arrays.asList(
            OrderStatus.COOKING,
            OrderStatus.READY
    );

    public CookController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User cook = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cook not found"));
        
        List<Order> orders = orderService.findOrdersForCook(cook.getId());
        List<Order> completedOrders = orderService.findCompletedByCook(cook.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("statuses", COOK_STATUSES);
        model.addAttribute("currentUserId", cook.getId());
        return "cook/dashboard";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Integer id, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Order order = orderService.findByIdWithItems(id);
        User cook = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cook not found"));
        
        model.addAttribute("order", order);
        model.addAttribute("statuses", COOK_STATUSES);
        model.addAttribute("canChangeStatus", orderService.canCookChangeStatus(order, cook));
        model.addAttribute("canRollback", orderService.canRollbackStatus(order, cook, "ROLE_COOK"));
        model.addAttribute("currentUserId", cook.getId());
        return "cook/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Integer id,
                               @RequestParam OrderStatus status,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User cook = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cook not found"));
        Order order = orderService.findById(id);
        if (!orderService.canCookChangeStatus(order, cook)) {
            redirectAttributes.addFlashAttribute("error", "Nemôžete meniť stav tejto objednávky (už doručuje sa alebo doručená)");
            return "redirect:/cook";
        }
        if (!COOK_STATUSES.contains(status)) {
            redirectAttributes.addFlashAttribute("error", "Neplatný stav pre kuchára");
            return "redirect:/cook";
        }
        orderService.updateStatusWithAssignment(id, status, cook.getId());
        redirectAttributes.addFlashAttribute("success", "Stav objednávky aktualizovaný");
        return "redirect:/cook";
    }

    @PostMapping("/orders/{id}/rollback")
    public String rollbackStatus(@PathVariable Integer id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        User cook = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cook not found"));
        Order order = orderService.findById(id);
        if (!orderService.canCookChangeStatus(order, cook)) {
            redirectAttributes.addFlashAttribute("error", "Nemôžete meniť stav tejto objednávky (už doručuje sa alebo doručená)");
            return "redirect:/cook";
        }
        if (!orderService.canRollbackStatus(order, cook, "ROLE_COOK")) {
            redirectAttributes.addFlashAttribute("error", "Nemôžete vrátiť túto objednávku späť");
            return "redirect:/cook";
        }
        
        OrderStatus previousStatus = orderService.getPreviousStatus(order.getStatus());
        orderService.updateStatus(id, previousStatus);
        redirectAttributes.addFlashAttribute("success", "Stav objednávky vrátený späť");
        return "redirect:/cook";
    }
}


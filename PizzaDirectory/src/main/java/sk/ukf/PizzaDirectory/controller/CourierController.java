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
 * Controller for courier dashboard - manages order delivery
 */
@Controller
@RequestMapping("/courier")
public class CourierController {

    private final OrderService orderService;
    private final UserService userService;

    // Statuses available for courier
    private static final List<OrderStatus> COURIER_STATUSES = Arrays.asList(
            OrderStatus.DELIVERING,
            OrderStatus.DELIVERED
    );

    public CourierController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User courier = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Courier not found"));
        
        List<Order> orders = orderService.findOrdersForCourier(courier.getId());
        List<Order> completedOrders = orderService.findCompletedByCourier(courier.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("statuses", COURIER_STATUSES);
        model.addAttribute("currentUserId", courier.getId());
        return "courier/dashboard";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Integer id, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Order order = orderService.findByIdWithItems(id);
        User courier = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Courier not found"));
        
        model.addAttribute("order", order);
        model.addAttribute("statuses", COURIER_STATUSES);
        model.addAttribute("canRollback", orderService.canRollbackStatus(order, courier, "ROLE_COURIER"));
        model.addAttribute("currentUserId", courier.getId());
        return "courier/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Integer id,
                               @RequestParam OrderStatus status,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User courier = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Courier not found"));
        
        // Validate that courier can only set DELIVERING or DELIVERED
        if (!COURIER_STATUSES.contains(status)) {
            redirectAttributes.addFlashAttribute("error", "Neplatný stav pre kuriéra");
            return "redirect:/courier";
        }
        
        orderService.updateStatusWithAssignment(id, status, courier.getId());
        redirectAttributes.addFlashAttribute("success", "Stav objednávky aktualizovaný");
        return "redirect:/courier";
    }

    @PostMapping("/orders/{id}/rollback")
    public String rollbackStatus(@PathVariable Integer id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        User courier = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Courier not found"));
        
        Order order = orderService.findById(id);
        
        if (!orderService.canRollbackStatus(order, courier, "ROLE_COURIER")) {
            redirectAttributes.addFlashAttribute("error", "Nemôžete vrátiť túto objednávku späť");
            return "redirect:/courier";
        }
        
        OrderStatus previousStatus = orderService.getPreviousStatus(order.getStatus());
        orderService.updateStatus(id, previousStatus);
        redirectAttributes.addFlashAttribute("success", "Stav objednávky vrátený späť");
        return "redirect:/courier";
    }
}


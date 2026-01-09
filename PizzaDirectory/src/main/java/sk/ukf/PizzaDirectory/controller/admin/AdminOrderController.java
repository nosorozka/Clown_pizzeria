package sk.ukf.PizzaDirectory.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.OrderStatus;
import sk.ukf.PizzaDirectory.service.OrderService;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status, Model model) {
        if (status != null) {
            model.addAttribute("orders", orderService.findByStatus(status));
        } else {
            model.addAttribute("orders", orderService.findAll());
        }
        model.addAttribute("statuses", Arrays.asList(OrderStatus.values()));
        model.addAttribute("selectedStatus", status);
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        model.addAttribute("order", orderService.findByIdWithItems(id));
        model.addAttribute("statuses", Arrays.asList(OrderStatus.values()));
        return "admin/orders/view";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes
    ) {
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Stav objednávky aktualizovaný");
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        orderService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Objednávka vymazaná");
        return "redirect:/admin/orders";
    }
}

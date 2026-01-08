package sk.ukf.PizzaDirectory.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.Size;
import sk.ukf.PizzaDirectory.service.SizeService;

@Controller
@RequestMapping("/admin/sizes")
public class AdminSizeController {

    private final SizeService sizeService;

    public AdminSizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sizes", sizeService.findAll());
        return "admin/sizes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("size", new Size());
        return "admin/sizes/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("size", sizeService.findById(id));
        return "admin/sizes/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("size") Size size,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/sizes/form";
        }
        sizeService.save(size);
        redirectAttributes.addFlashAttribute("success", "Size saved successfully");
        return "redirect:/admin/sizes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        sizeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Size deleted successfully");
        return "redirect:/admin/sizes";
    }
}


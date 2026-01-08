package sk.ukf.PizzaDirectory.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.Tag;
import sk.ukf.PizzaDirectory.service.TagService;

@Controller
@RequestMapping("/admin/tags")
public class AdminTagController {

    private final TagService tagService;

    public AdminTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tags", tagService.findAll());
        return "admin/tags/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin/tags/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("tag", tagService.findById(id));
        return "admin/tags/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute Tag tag,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/tags/form";
        }
        tagService.save(tag);
        redirectAttributes.addFlashAttribute("success", "Tag saved successfully");
        return "redirect:/admin/tags";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        tagService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Tag deleted successfully");
        return "redirect:/admin/tags";
    }
}


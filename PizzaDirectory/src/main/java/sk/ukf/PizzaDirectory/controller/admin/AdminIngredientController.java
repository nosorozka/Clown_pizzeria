package sk.ukf.PizzaDirectory.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.Ingredient;
import sk.ukf.PizzaDirectory.service.IngredientService;

@Controller
@RequestMapping("/admin/ingredients")
public class AdminIngredientController {

    private final IngredientService ingredientService;

    public AdminIngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ingredients", ingredientService.findAll());
        return "admin/ingredients/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "admin/ingredients/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("ingredient", ingredientService.findById(id));
        return "admin/ingredients/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute Ingredient ingredient,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/ingredients/form";
        }
        ingredientService.save(ingredient);
        redirectAttributes.addFlashAttribute("success", "Ingredient saved successfully");
        return "redirect:/admin/ingredients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ingredientService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Ingredient deleted successfully");
        return "redirect:/admin/ingredients";
    }
}


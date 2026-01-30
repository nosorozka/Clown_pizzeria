package sk.ukf.PizzaDirectory.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.dto.PizzaFormDto;
import sk.ukf.PizzaDirectory.service.IngredientService;
import sk.ukf.PizzaDirectory.service.PizzaService;
import sk.ukf.PizzaDirectory.service.SizeService;
import sk.ukf.PizzaDirectory.service.TagService;

@Controller
@RequestMapping("/admin/pizza")
public class AdminPizzaController {

    private final PizzaService pizzaService;
    private final IngredientService ingredientService;
    private final SizeService sizeService;
    private final TagService tagService;

    public AdminPizzaController(PizzaService pizzaService,
                                IngredientService ingredientService,
                                SizeService sizeService,
                                TagService tagService) {
        this.pizzaService = pizzaService;
        this.ingredientService = ingredientService;
        this.sizeService = sizeService;
        this.tagService = tagService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pizzas", pizzaService.findAll());
        return "admin/pizza/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pizzaForm", new PizzaFormDto());
        addFormAttributes(model);
        return "admin/pizza/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("pizzaForm", pizzaService.findFormByIdForEdit(id));
        addFormAttributes(model);
        return "admin/pizza/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("pizzaForm") PizzaFormDto pizzaForm,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/pizza/form";
        }

        pizzaService.saveFromForm(pizzaForm, imageFile);

        redirectAttributes.addFlashAttribute("success", "Pizza saved successfully");
        return "redirect:/admin/pizza";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        // Soft delete - no need to delete image file
        pizzaService.softDelete(id);
        redirectAttributes.addFlashAttribute("success", "Pizza soft deleted successfully");
        return "redirect:/admin/pizza";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("allIngredients", ingredientService.findAll());
        model.addAttribute("allSizes", sizeService.findAll());
        model.addAttribute("allTags", tagService.findAll());
    }
}

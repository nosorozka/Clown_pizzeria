package sk.ukf.PizzaDirectory.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.Ingredient;
import sk.ukf.PizzaDirectory.entity.Pizza;
import sk.ukf.PizzaDirectory.entity.Size;
import sk.ukf.PizzaDirectory.entity.Tag;
import sk.ukf.PizzaDirectory.service.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/pizza")
public class AdminPizzaController {

    private final PizzaService pizzaService;
    private final IngredientService ingredientService;
    private final SizeService sizeService;
    private final TagService tagService;
    private final FileStorageService fileStorageService;

    public AdminPizzaController(PizzaService pizzaService, IngredientService ingredientService,
                                SizeService sizeService, TagService tagService,
                                FileStorageService fileStorageService) {
        this.pizzaService = pizzaService;
        this.ingredientService = ingredientService;
        this.sizeService = sizeService;
        this.tagService = tagService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pizzas", pizzaService.findAll());
        return "admin/pizza/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pizza", new Pizza());
        addFormAttributes(model);
        return "admin/pizza/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Pizza pizza = pizzaService.findByIdWithDetails(id);
        model.addAttribute("pizza", pizza);
        addFormAttributes(model);
        return "admin/pizza/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute Pizza pizza,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) List<Integer> ingredientIds,
            @RequestParam(required = false) List<Integer> sizeIds,
            @RequestParam(required = false) List<Integer> tagIds,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/pizza/form";
        }

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if updating
            if (pizza.getId() != null) {
                Pizza existing = pizzaService.findById(pizza.getId());
                if (existing.getImagePath() != null) {
                    fileStorageService.deleteFile(existing.getImagePath());
                }
            }
            String filename = fileStorageService.storeFile(imageFile);
            pizza.setImagePath(filename);
        } else if (pizza.getId() != null) {
            // Keep existing image
            Pizza existing = pizzaService.findById(pizza.getId());
            pizza.setImagePath(existing.getImagePath());
        }

        // Handle relationships
        Set<Ingredient> ingredients = new HashSet<>();
        if (ingredientIds != null) {
            for (Integer id : ingredientIds) {
                ingredients.add(ingredientService.findById(id));
            }
        }
        pizza.setIngredients(ingredients);

        Set<Size> sizes = new HashSet<>();
        if (sizeIds != null) {
            for (Integer id : sizeIds) {
                sizes.add(sizeService.findById(id));
            }
        }
        pizza.setSizes(sizes);

        Set<Tag> tags = new HashSet<>();
        if (tagIds != null) {
            for (Integer id : tagIds) {
                tags.add(tagService.findById(id));
            }
        }
        pizza.setTags(tags);

        pizzaService.save(pizza);
        redirectAttributes.addFlashAttribute("success", "Pizza saved successfully");
        return "redirect:/admin/pizza";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Pizza pizza = pizzaService.findById(id);
        if (pizza.getImagePath() != null) {
            fileStorageService.deleteFile(pizza.getImagePath());
        }
        pizzaService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Pizza deleted successfully");
        return "redirect:/admin/pizza";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("allIngredients", ingredientService.findAll());
        model.addAttribute("allSizes", sizeService.findAll());
        model.addAttribute("allTags", tagService.findAll());
    }
}


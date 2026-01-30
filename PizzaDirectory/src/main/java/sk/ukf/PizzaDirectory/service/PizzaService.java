package sk.ukf.PizzaDirectory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.ukf.PizzaDirectory.dto.PizzaFormDto;
import sk.ukf.PizzaDirectory.entity.Ingredient;
import sk.ukf.PizzaDirectory.entity.Pizza;
import sk.ukf.PizzaDirectory.entity.PizzaSize;
import sk.ukf.PizzaDirectory.entity.Tag;
import sk.ukf.PizzaDirectory.exception.ResourceNotFoundException;
import sk.ukf.PizzaDirectory.repository.PizzaRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final FileStorageService fileStorageService;
    private final IngredientService ingredientService;
    private final SizeService sizeService;
    private final TagService tagService;

    public PizzaService(PizzaRepository pizzaRepository, FileStorageService fileStorageService,
                       IngredientService ingredientService, SizeService sizeService, TagService tagService) {
        this.pizzaRepository = pizzaRepository;
        this.fileStorageService = fileStorageService;
        this.ingredientService = ingredientService;
        this.sizeService = sizeService;
        this.tagService = tagService;
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAll() {
        return pizzaRepository.findByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAllWithTags() {
        return pizzaRepository.findAllWithTags();
    }

    @Transactional(readOnly = true)
    public Page<Pizza> findAllWithTags(Pageable pageable) {
        List<Pizza> allPizzas = pizzaRepository.findAllWithTags();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPizzas.size());
        
        if (start > allPizzas.size()) {
            return new PageImpl<>(List.of(), pageable, allPizzas.size());
        }
        
        return new PageImpl<>(allPizzas.subList(start, end), pageable, allPizzas.size());
    }

    @Transactional(readOnly = true)
    public Pizza findById(Integer id) {
        return pizzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza", id));
    }

    @Transactional(readOnly = true)
    public Pizza findByIdWithDetails(Integer id) {
        return pizzaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza", id));
    }

    @Transactional(readOnly = true)
    public Pizza findBySlug(String slug) {
        return pizzaRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza with slug: " + slug));
    }

    @Transactional(readOnly = true)
    public List<Pizza> findByTagId(Integer tagId) {
        return pizzaRepository.findByTagId(tagId);
    }

    @Transactional(readOnly = true)
    public List<Pizza> searchByName(String name) {
        return pizzaRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name);
    }

    public Pizza save(Pizza pizza) {
        // Generate slug if not set or pizza is new
        if (pizza.getSlug() == null || pizza.getSlug().isEmpty()) {
            pizza.setSlug(generateSlug(pizza.getName()));
        }
        return pizzaRepository.save(pizza);
    }

    public Pizza saveWithDetails(Integer pizzaId, String name, java.math.BigDecimal price, 
                                String description, MultipartFile imageFile,
                                List<Integer> ingredientIds, List<Integer> sizeIds, 
                                List<Integer> tagIds) {
        Pizza pizzaToSave;
        
        // If updating existing pizza, load the managed entity
        if (pizzaId != null) {
            pizzaToSave = findByIdWithDetails(pizzaId);
            pizzaToSave.setName(name);
            pizzaToSave.setPrice(price);
            pizzaToSave.setDescription(description);
            
            // Remove deleted ingredients from existing pizza
            pizzaToSave.getIngredients().removeIf(ingredient -> ingredient.isDeleted());
            
            // Clear existing relationships to handle removed items
            Set<Ingredient> ingredientsToRemove = new HashSet<>(pizzaToSave.getIngredients());
            Set<PizzaSize> sizesToRemove = new HashSet<>(pizzaToSave.getSizes());
            Set<Tag> tagsToRemove = new HashSet<>(pizzaToSave.getTags());
            
            for (Ingredient ingredient : ingredientsToRemove) {
                pizzaToSave.removeIngredient(ingredient);
            }
            for (PizzaSize size : sizesToRemove) {
                pizzaToSave.removeSize(size);
            }
            for (Tag tag : tagsToRemove) {
                pizzaToSave.removeTag(tag);
            }
        } else {
            // Creating new pizza
            pizzaToSave = new Pizza(name, price);
            pizzaToSave.setDescription(description);
        }

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if updating
            if (pizzaToSave.getImagePath() != null) {
                fileStorageService.deleteFile(pizzaToSave.getImagePath());
            }
            String filename = fileStorageService.storeFile(imageFile);
            pizzaToSave.setImagePath(filename);
        }

        // Add selected ingredients (only active ones)
        if (ingredientIds != null) {
            for (Integer id : ingredientIds) {
                Ingredient ingredient = ingredientService.findById(id);
                // Skip deleted ingredients
                if (!ingredient.isDeleted()) {
                    pizzaToSave.addIngredient(ingredient);
                }
            }
        }

        // Add selected sizes
        if (sizeIds != null) {
            for (Integer id : sizeIds) {
                PizzaSize size = sizeService.findById(id);
                pizzaToSave.addSize(size);
            }
        }

        // Add selected tags
        if (tagIds != null) {
            for (Integer id : tagIds) {
                Tag tag = tagService.findById(id);
                pizzaToSave.addTag(tag);
            }
        }

        // Save with automatic slug generation
        return save(pizzaToSave);
    }

    /**
     * Build and save Pizza entity from admin form DTO.
     * Keeps controller/view decoupled from JPA entities and relationship wiring.
     */
    public Pizza saveFromForm(PizzaFormDto form, MultipartFile imageFile) {
        if (form == null) {
            throw new IllegalArgumentException("Pizza form must not be null");
        }
        if (form.getIngredientIds() == null || form.getIngredientIds().isEmpty()) {
            throw new IllegalArgumentException("At least one ingredient is required");
        }
        if (form.getSizeIds() == null || form.getSizeIds().isEmpty()) {
            throw new IllegalArgumentException("At least one size is required");
        }
        return saveWithDetails(
                form.getId(),
                form.getName(),
                form.getPrice(),
                form.getDescription(),
                imageFile,
                form.getIngredientIds(),
                form.getSizeIds(),
                form.getTagIds()
        );
    }

    /**
     * Build admin form DTO for editing an existing pizza.
     */
    @Transactional(readOnly = true)
    public PizzaFormDto findFormByIdForEdit(Integer id) {
        Pizza pizza = findByIdWithDetailsForEdit(id);

        PizzaFormDto form = new PizzaFormDto();
        form.setImagePath(pizza.getImagePath());
        form.setId(pizza.getId());
        form.setName(pizza.getName());
        form.setPrice(pizza.getPrice());
        form.setDescription(pizza.getDescription());

        // Preselect only active ingredients (deleted ones are filtered in findByIdWithDetailsForEdit)
        if (pizza.getIngredients() != null) {
            form.setIngredientIds(pizza.getIngredients().stream().map(Ingredient::getId).toList());
        }
        if (pizza.getSizes() != null) {
            form.setSizeIds(pizza.getSizes().stream().map(PizzaSize::getId).toList());
        }
        if (pizza.getTags() != null) {
            form.setTagIds(pizza.getTags().stream().map(Tag::getId).toList());
        }
        return form;
    }

    @Transactional(readOnly = true)
    public Pizza findByIdWithDetailsForEdit(Integer id) {
        Pizza pizza = findByIdWithDetails(id);
        // Filter out deleted ingredients before displaying form
        pizza.getIngredients().removeIf(ingredient -> ingredient.isDeleted());
        return pizza;
    }

    public void deleteById(Integer id) {
        Pizza pizza = findById(id);
        softDelete(id);
    }

    public void softDelete(Integer id) {
        Pizza pizza = findById(id);
        pizza.softDelete();
        pizzaRepository.save(pizza);
    }

    // Generate slug from pizza name
    public String generateSlug(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        
        // Normalize and remove accents
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{M}", "");
        
        // Convert to lowercase and replace spaces with hyphens
        slug = slug.toLowerCase()
                   .trim()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-");
        
        // Check for uniqueness and append number if needed
        String originalSlug = slug;
        int counter = 1;
        while (pizzaRepository.findBySlug(slug).isPresent()) {
            slug = originalSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
}


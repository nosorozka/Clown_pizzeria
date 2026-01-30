package sk.ukf.PizzaDirectory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Admin Pizza create/edit form.
 * Keeps controllers/views decoupled from JPA entities.
 */
public class PizzaFormDto {

    private Integer id;

    @NotBlank(message = "Názov pizze je povinný")
    @Size(max = 45, message = "Názov pizze nesmie presiahnuť 45 znakov")
    private String name;

    @NotNull(message = "Cena je povinná")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musí byť väčšia ako 0")
    private BigDecimal price;

    private String description;

    @NotEmpty(message = "Vyber aspoň 1 ingredienciu")
    private List<Integer> ingredientIds = new ArrayList<>();

    @NotEmpty(message = "Vyber aspoň 1 veľkosť")
    private List<Integer> sizeIds = new ArrayList<>();
    private List<Integer> tagIds = new ArrayList<>();

    public PizzaFormDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    private String imagePath;

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Integer> getIngredientIds() { return ingredientIds; }
    public void setIngredientIds(List<Integer> ingredientIds) {
        this.ingredientIds = (ingredientIds != null) ? ingredientIds : new ArrayList<>();
    }

    public List<Integer> getSizeIds() { return sizeIds; }
    public void setSizeIds(List<Integer> sizeIds) {
        this.sizeIds = (sizeIds != null) ? sizeIds : new ArrayList<>();
    }

    public List<Integer> getTagIds() { return tagIds; }
    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = (tagIds != null) ? tagIds : new ArrayList<>();
    }
}

package sk.ukf.PizzaDirectory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizza")
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Názov pizze je povinný")
    @Size(max = 45, message = "Názov pizze nesmie presiahnuť 45 znakov")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Cena je povinná")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musí byť väčšia ako 0")
    @Column(name = "pizza_price", nullable = false)
    private BigDecimal price;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "description")
    private String description;

    @Size(max = 100, message = "Slug nesmie presiahnuť 100 znakov")
    @Column(name = "slug", unique = true, length = 100)
    private String slug;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pizza_has_ingredients",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredients_id")
    )
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pizza_has_size",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private Set<PizzaSize> sizes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pizza_has_tags",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Pizza() {
    }

    public Pizza(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<PizzaSize> getSizes() {
        return sizes;
    }

    public void setSizes(Set<PizzaSize> sizes) {
        this.sizes = sizes;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // Helper methods for managing relationships
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        ingredient.getPizzas().add(this);
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
        ingredient.getPizzas().remove(this);
    }

    public void addSize(PizzaSize size) {
        this.sizes.add(size);
        size.getPizzas().add(this);
    }

    public void removeSize(PizzaSize size) {
        this.sizes.remove(size);
        size.getPizzas().remove(this);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPizzas().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPizzas().remove(this);
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public java.time.LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(java.time.LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Soft delete helper method
    public void softDelete() {
        this.deleted = true;
        this.deletedAt = java.time.LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deleted != null && deleted;
    }
}

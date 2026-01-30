package sk.ukf.PizzaDirectory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Názov ingrediencie je povinný")
    @Size(max = 45, message = "Názov ingrediencie nesmie presiahnuť 45 znakov")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Cena je povinná")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musí byť väčšia ako 0")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    @ManyToMany(mappedBy = "ingredients")
    private Set<Pizza> pizzas = new HashSet<>();

    public Ingredient() {
    }

    public Ingredient(String name, BigDecimal price) {
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

    public Set<Pizza> getPizzas() {
        return pizzas;
    }

    public void setPizzas(Set<Pizza> pizzas) {
        this.pizzas = pizzas;
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

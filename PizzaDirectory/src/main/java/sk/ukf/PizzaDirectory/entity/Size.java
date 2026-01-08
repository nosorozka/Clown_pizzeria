package sk.ukf.PizzaDirectory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "size")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Názov veľkosti je povinný")
    @jakarta.validation.constraints.Size(max = 45, message = "Názov veľkosti nesmie presiahnuť 45 znakov")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Cena je povinná")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cena musí byť 0 alebo viac")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToMany(mappedBy = "sizes")
    private Set<Pizza> pizzas = new HashSet<>();

    public Size() {
    }

    public Size(String name, BigDecimal price) {
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
}

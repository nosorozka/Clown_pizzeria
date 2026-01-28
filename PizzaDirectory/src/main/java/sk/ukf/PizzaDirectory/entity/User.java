package sk.ukf.PizzaDirectory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Meno je povinné")
    @Size(max = 45, message = "Meno nesmie presiahnuť 45 znakov")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Priezvisko je povinné")
    @Size(max = 45, message = "Priezvisko nesmie presiahnuť 45 znakov")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Dátum narodenia musí byť v minulosti")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email je povinný")
    @Email(message = "Email musí byť platný")
    @Size(max = 45, message = "Email nesmie presiahnuť 45 znakov")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Heslo je povinné")
    @Size(max = 255, message = "Heslo nesmie presiahnuť 255 znakov")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 255, message = "Adresa nesmie presiahnuť 255 znakov")
    @Column(name = "address")
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public User() {
    }

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = LocalDate.now();
//        this.updatedAt = LocalDate.now();
//    }

    //@PreUpdate
    //protected void onUpdate() { this.updatedAt = LocalDate.now(); }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    //public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

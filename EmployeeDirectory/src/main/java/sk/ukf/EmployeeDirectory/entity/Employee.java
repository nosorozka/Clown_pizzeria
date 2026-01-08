package sk.ukf.EmployeeDirectory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Meno je povinné")
    @Size(min = 2, max = 50, message = "Meno musí mať 2-50 znakov")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Priezvisko je povinné")
    @Size(min = 2, max = 50, message = "Priezvisko musí mať 2-50 znakov")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Dátum narodenia je povinný")
    @Past(message = "Dátum narodenia musí byť v minulosti")
    @Column(name = "birthDate")
    private Date birthDate;

    @NotBlank(message = "Email je povinný")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Neplatná e-mailová adresa"
    )
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Telefónne číslo je povinné")
    @Pattern(
            regexp = "^\\+421\\d{9}$",
            message = "Telefónne číslo musí začínať +421 a obsahovať 9 číslic za ním"
    )
    @Column(name = "phone")
    private String phone;

    @Size(min = 2, max = 100, message = "Pracovná pozícia musí mať 2-100 znakov")
    @Column(name = "jobTitle")
    private String jobTitle;

    @NotNull(message = "Plat je povinný")
    @Positive(message = "Plat musí byť kladné číslo")
    @Column(name = "salary")
    private Double salary;

    @Column(name = "fullTime")
    private Boolean fullTime;

    public Employee() {
    }

    public Employee(String firstName, String lastName, Date birthDate,
                    String email, String phone, String jobTitle, Double salary, Boolean fullTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.fullTime = fullTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getBirth_date() {
        return birthDate;
    }

    public void setBirth_date(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob_title() {
        return jobTitle;
    }

    public void setJob_title(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Boolean getFull_time() {
        return fullTime;
    }

    public void setFull_time(Boolean fullTime) {
        this.fullTime = fullTime;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", salary=" + salary +
                ", fullTime=" + fullTime +
                '}';
    }
}
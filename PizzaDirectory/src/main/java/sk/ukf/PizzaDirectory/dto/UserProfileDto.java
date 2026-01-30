package sk.ukf.PizzaDirectory.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserProfileDto {

    @NotBlank(message = "Meno je povinné")
    @Size(max = 45, message = "Meno nesmie presiahnuť 45 znakov")
    private String firstName;

    @NotBlank(message = "Priezvisko je povinné")
    @Size(max = 45, message = "Priezvisko nesmie presiahnuť 45 znakov")
    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Dátum narodenia musí byť v minulosti")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Adresa nesmie presiahnuť 255 znakov")
    private String address;

    private String email;

    public UserProfileDto() {
    }

    public UserProfileDto(String firstName, String lastName, LocalDate dateOfBirth, String address, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

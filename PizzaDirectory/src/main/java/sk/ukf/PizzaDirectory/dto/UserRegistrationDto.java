package sk.ukf.PizzaDirectory.dto;

import jakarta.validation.constraints.*;

/**
 * DTO for user registration form
 */
public class UserRegistrationDto {

    @NotBlank(message = "Meno je povinné")
    @Size(max = 45, message = "Meno nesmie presiahnuť 45 znakov")
    private String firstName;

    @NotBlank(message = "Priezvisko je povinné")
    @Size(max = 45, message = "Priezvisko nesmie presiahnuť 45 znakov")
    private String lastName;

    @NotBlank(message = "Email je povinný")
    @Email(message = "Email musí byť platný")
    @Size(max = 45, message = "Email nesmie presiahnuť 45 znakov")
    private String email;

    @NotBlank(message = "Heslo je povinné")
    @Size(min = 6, max = 100, message = "Heslo musí mať 6 až 100 znakov")
    private String password;

    @NotBlank(message = "Potvrdenie hesla je povinné")
    private String confirmPassword;

    @Size(max = 255, message = "Adresa nesmie presiahnuť 255 znakov")
    private String address;

    public UserRegistrationDto() {
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}

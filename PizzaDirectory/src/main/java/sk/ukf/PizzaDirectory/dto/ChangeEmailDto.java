package sk.ukf.PizzaDirectory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class ChangeEmailDto {

    @NotBlank(message = "Nový email je povinný")
    @Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Zadajte platnú emailovú adresu"
    )
    @Size(max = 255)
    private String newEmail;

    @NotBlank(message = "Heslo je povinné na potvrdenie")
    private String confirmPassword;

    public ChangeEmailDto() {
    }

    public ChangeEmailDto(String newEmail, String confirmPassword) {
        this.newEmail = newEmail;
        this.confirmPassword = confirmPassword;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

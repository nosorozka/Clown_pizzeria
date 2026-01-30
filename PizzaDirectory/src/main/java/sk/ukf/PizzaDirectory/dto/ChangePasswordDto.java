package sk.ukf.PizzaDirectory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;


public class ChangePasswordDto {

    @NotBlank(message = "Staré heslo je povinné")
    private String oldPassword;

    @NotBlank(message = "Nové heslo je povinné")
    @Pattern(
    regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
    message = "Heslo musí mať aspoň 8 znakov, jednu veľkú písmeno, číslo a špeciálny znak"
    )
    private String newPassword;

    @NotBlank(message = "Potvrdenie hesla je povinné")
    private String confirmPassword;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}

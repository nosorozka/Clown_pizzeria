package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.dto.ChangeEmailDto;
import sk.ukf.PizzaDirectory.dto.ChangePasswordDto;
import sk.ukf.PizzaDirectory.dto.UserProfileDto;
import sk.ukf.PizzaDirectory.entity.User;
import sk.ukf.PizzaDirectory.service.CartService;
import sk.ukf.PizzaDirectory.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final CartService cartService;

    public ProfileController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, 
                             Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, 
                                 Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserProfileDto dto = new UserProfileDto(
            user.getFirstName(),
            user.getLastName(),
            user.getDateOfBirth(),
            user.getAddress(),
            user.getEmail()
        );
        
        model.addAttribute("profileDto", dto);
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                               @Valid @ModelAttribute("profileDto") UserProfileDto dto,
                               BindingResult bindingResult,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/edit";
        }
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userService.updateProfile(user.getId(), dto.getFirstName(), dto.getLastName(),
                dto.getDateOfBirth(), dto.getAddress());
        
        redirectAttributes.addFlashAttribute("success", "Profil bol úspešne aktualizovaný");
        return "redirect:/profile";
    }

    @PostMapping("/avatar")
    public String uploadAvatar(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam("avatarFile") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Prosím, vyberte súbor");
            return "redirect:/profile/edit";
        }
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            userService.uploadAvatar(user.getId(), file);
            redirectAttributes.addFlashAttribute("success", "Avatar bol úspešne nahraný");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Chyba pri nahrávaní avatara");
        }
        
        return "redirect:/profile/edit";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(@AuthenticationPrincipal UserDetails userDetails,
                                    Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("passwordDto", new ChangePasswordDto());
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("passwordDto") ChangePasswordDto dto,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/change-password";
        }
        
        if (!dto.passwordsMatch()) {
            bindingResult.rejectValue("confirmPassword", "error.passwordDto", 
                                     "Heslá sa nezhodujú");
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/change-password";
        }
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            userService.changePassword(user.getId(), dto.getOldPassword(), dto.getNewPassword());
            redirectAttributes.addFlashAttribute("success", "Heslo bolo úspešne zmenené");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("oldPassword", "error.passwordDto", 
                                     "Staré heslo je nesprávne");
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/change-password";
        }
    }

    @GetMapping("/change-email")
    public String changeEmailForm(@AuthenticationPrincipal UserDetails userDetails,
                                 Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("emailDto", new ChangeEmailDto());
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "profile/change-email";
    }

    @PostMapping("/change-email")
    public String changeEmail(@AuthenticationPrincipal UserDetails userDetails,
                             @Valid @ModelAttribute("emailDto") ChangeEmailDto dto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/change-email";
        }
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            userService.changeEmail(user.getId(), dto.getNewEmail(), dto.getConfirmPassword());
            session.invalidate();
            redirectAttributes.addFlashAttribute("success", "Email bol úspešne zmenený. Prihláste sa znova s novým emailom.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("confirmPassword", "error.emailDto", "Heslo je nesprávne");
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/change-email";
        } catch (sk.ukf.PizzaDirectory.exception.EmailAlreadyExistsException e) {
            bindingResult.rejectValue("newEmail", "error.emailDto", "Tento email už používa iný účet");
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            return "profile/change-email";
        }
    }
}

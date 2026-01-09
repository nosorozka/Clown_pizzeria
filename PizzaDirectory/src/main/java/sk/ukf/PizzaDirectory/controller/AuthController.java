package sk.ukf.PizzaDirectory.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.dto.UserRegistrationDto;
import sk.ukf.PizzaDirectory.exception.EmailAlreadyExistsException;
import sk.ukf.PizzaDirectory.service.CartService;
import sk.ukf.PizzaDirectory.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final CartService cartService;

    public AuthController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        
        // Check password match
        if (!dto.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match");
        }
        
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.user", e.getMessage());
            return "auth/register";
        }
    }
}


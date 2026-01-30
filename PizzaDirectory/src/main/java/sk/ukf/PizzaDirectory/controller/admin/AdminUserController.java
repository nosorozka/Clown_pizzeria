package sk.ukf.PizzaDirectory.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.ukf.PizzaDirectory.entity.RoleName;
import sk.ukf.PizzaDirectory.entity.User;
import sk.ukf.PizzaDirectory.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private static final int PAGE_SIZE = 10;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<User> userPage = userService.findAllIncludingDeleted(pageable);
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        
        return "admin/users/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", RoleName.values());
        return "admin/users/view";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.softDelete(id);
            redirectAttributes.addFlashAttribute("success", "Používateľ bol deaktivovaný");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Chyba pri deaktivácii používateľa");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.activate(id);
            redirectAttributes.addFlashAttribute("success", "Používateľ bol aktivovaný");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Chyba pri aktivácii používateľa");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/change-role")
    public String changeRole(@PathVariable Integer id,
                            @RequestParam RoleName role,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.changeRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Rola používateľa bola zmenená");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Chyba pri zmene role");
        }
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePermanently(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deletePermanently(id);
            redirectAttributes.addFlashAttribute("success", "Používateľ bol natrvalo vymazaný");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Chyba pri mazaní používateľa");
        }
        return "redirect:/admin/users";
    }
}

package vn.iostar.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.iostar.entity.Role;
import vn.iostar.entity.User;
import vn.iostar.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        User u = authService.authenticate(email, password);
        if (u == null) {
            model.addAttribute("error", "Sai email hoặc mật khẩu");
            return "login";
        }
        session.setAttribute("USER", u);
        session.setAttribute("ROLE", u.getRole()); // USER / ADMIN
        if (u.getRole() == Role.ADMIN) return "redirect:/admin/home";
        return "redirect:/user/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

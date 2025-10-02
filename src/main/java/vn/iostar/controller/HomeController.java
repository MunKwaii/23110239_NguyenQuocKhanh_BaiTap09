package vn.iostar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class HomeController {
    @GetMapping("/user/home")
    public String userHome() { return "user-home"; }

    @GetMapping("/admin/home")
    public String adminHome() { return "admin-home"; }
}
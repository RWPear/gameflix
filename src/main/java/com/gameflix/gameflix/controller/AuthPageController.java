package com.gameflix.gameflix.controller;

import com.gameflix.gameflix.login.LoginRequest;
import com.gameflix.gameflix.login.RegisterRequest;
import com.gameflix.gameflix.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPageController {

    private final UserService userService;

    public AuthPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth")
    public String auth(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/account";
        }
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth";
    }

    @PostMapping("/auth/login")
    public String login(@ModelAttribute("loginRequest") LoginRequest req,
                        RedirectAttributes redirectAttributes,
                        Model model,
                        HttpSession session) {
        if (session.getAttribute("username") != null) {
            redirectAttributes.addFlashAttribute("loginSuccess", "Already signed in as " + session.getAttribute("username"));
            return "redirect:/account";
        }
        boolean ok = userService.login(req.getUsername(), req.getPassword());
        if (ok) {
            session.setAttribute("username", req.getUsername());
            redirectAttributes.addFlashAttribute("loginSuccess", "Login successful. Welcome, " + req.getUsername() + "!");
            return "redirect:/catalog";
        }
        model.addAttribute("loginError", "Invalid username or password");
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute("registerRequest") RegisterRequest req,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (req.getUsername() == null || req.getPassword() == null || req.getUsername().isBlank() || req.getPassword().isBlank()) {
            model.addAttribute("registerError", "Username and password are required");
            model.addAttribute("loginRequest", new LoginRequest());
            return "auth";
        }
        if (userService.usernameExists(req.getUsername())) {
            model.addAttribute("registerError", "Username already exists");
            model.addAttribute("loginRequest", new LoginRequest());
            return "auth";
        }
        userService.register(req.getUsername(), req.getPassword());
        redirectAttributes.addFlashAttribute("registerSuccess", "User registered successfully. You can now log in.");
        return "redirect:/auth";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("loginSuccess", "Signed out.");
        return "redirect:/";
    }
}

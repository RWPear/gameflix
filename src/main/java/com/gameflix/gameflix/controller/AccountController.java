package com.gameflix.gameflix.controller;

import com.gameflix.gameflix.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    private final LibraryService libraryService;

    public AccountController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        Object user = session.getAttribute("username");
        if (user == null) {
            return "redirect:/auth";
        }
        String username = user.toString();
        model.addAttribute("library", libraryService.listForUser(username));
        return "account";
    }
}

package com.gameflix.gameflix.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUser")
    public String currentUser(HttpSession session) {
        Object u = session.getAttribute("username");
        return u != null ? u.toString() : "Guest";
    }

    @ModelAttribute("planTier")
    public String planTier(HttpSession session) {
        Object p = session.getAttribute("planTier");
        return p != null ? p.toString() : "Starter";
    }
}

package com.gameflix.gameflix.config;

import com.gameflix.gameflix.model.PlanTier;
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
        String normalized = PlanTier.normalize(p != null ? p.toString() : null);
        if (normalized == null) {
            normalized = PlanTier.FREE;
        }
        session.setAttribute("planTier", normalized);
        return normalized;
    }
}

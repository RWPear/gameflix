package com.gameflix.gameflix.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PlanController {

    @GetMapping("/select-plan/{tier}")
    public String selectPlan(@PathVariable String tier, HttpSession session, RedirectAttributes redirectAttributes) {
        session.setAttribute("pendingPlan", tier);
        redirectAttributes.addFlashAttribute("planSuccess", "Checkout for plan: " + tier);
        return "redirect:/plans/checkout";
    }

    @GetMapping("/plans/checkout")
    public String checkout() {
        return "plan_checkout";
    }

    @PostMapping("/plans/confirm")
    public String confirmPlan(HttpSession session, RedirectAttributes redirectAttributes) {
        Object pending = session.getAttribute("pendingPlan");
        if (pending != null) {
            session.setAttribute("planTier", pending.toString());
            session.removeAttribute("pendingPlan");
            redirectAttributes.addFlashAttribute("planSuccess", "Plan updated to " + session.getAttribute("planTier"));
        }
        return "redirect:/account";
    }
}

package com.gameflix.gameflix.controller;

import com.gameflix.gameflix.model.PlanTier;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PlanController {

    @GetMapping("/select-plan/{tier}")
    public String selectPlan(@PathVariable String tier, HttpSession session, RedirectAttributes redirectAttributes) {
        String normalized = normalizeOrDefault(tier, session);
        session.setAttribute("pendingPlan", normalized);
        redirectAttributes.addFlashAttribute("planSuccess", "Checkout preloaded with " + normalized);
        return "redirect:/plans/checkout?tier=" + normalized;
    }

    @GetMapping("/plans/checkout")
    public String checkout(@RequestParam(value = "tier", required = false) String tier,
                           HttpSession session,
                           Model model) {
        String normalizedCurrent = PlanTier.normalize((String) session.getAttribute("planTier"));
        if (normalizedCurrent == null) {
            normalizedCurrent = PlanTier.FREE;
        }

        String normalizedSelected = PlanTier.normalize(tier);
        if (normalizedSelected == null) {
            normalizedSelected = PlanTier.normalize((String) session.getAttribute("pendingPlan"));
        }
        if (normalizedSelected == null) {
            normalizedSelected = normalizedCurrent;
        }
        session.setAttribute("pendingPlan", normalizedSelected);

        final String current = normalizedCurrent;
        final String selected = normalizedSelected;

        List<PlanTier.PlanDescriptor> tiers = PlanTier.descriptors();
        PlanTier.PlanDescriptor selectedDescriptor = tiers.stream()
                .filter(t -> t.key().equals(selected))
                .findFirst()
                .orElse(tiers.get(0));
        PlanTier.PlanDescriptor currentDescriptor = tiers.stream()
                .filter(t -> t.key().equals(current))
                .findFirst()
                .orElse(selectedDescriptor);

        model.addAttribute("tiers", tiers);
        model.addAttribute("selectedDescriptor", selectedDescriptor);
        model.addAttribute("currentDescriptor", currentDescriptor);
        model.addAttribute("selectedTier", selected);
        model.addAttribute("currentTier", current);
        model.addAttribute("isUpgrade", PlanTier.weight(selected) > PlanTier.weight(current));
        return "plan_checkout";
    }

    @PostMapping("/plans/confirm")
    public String confirmPlan(@RequestParam(value = "tier", required = false) String tier,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String pending = PlanTier.normalize(tier);
        if (pending == null) {
            pending = PlanTier.normalize((String) session.getAttribute("pendingPlan"));
        }
        if (pending == null) {
            pending = PlanTier.FREE;
        }
        session.setAttribute("planTier", pending);
        session.setAttribute("pendingPlan", pending);
        redirectAttributes.addFlashAttribute("planSuccess", "Plan updated to " + pending);
        return "redirect:/account";
    }

    private String normalizeOrDefault(String tier, HttpSession session) {
        String normalized = PlanTier.normalize(tier);
        if (normalized == null) {
            normalized = PlanTier.normalize((String) session.getAttribute("planTier"));
        }
        if (normalized == null) {
            normalized = PlanTier.FREE;
        }
        return normalized;
    }
}

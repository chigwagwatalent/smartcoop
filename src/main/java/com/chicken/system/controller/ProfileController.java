// src/main/java/com/chicken/system/controller/ProfileController.java
package com.chicken.system.controller;

import com.chicken.system.dto.PasswordChangeForm;
import com.chicken.system.dto.ProfileUpdateForm;
import com.chicken.system.entity.User;
import com.chicken.system.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserServices userServices;

    public ProfileController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping
    public String profile(Model model, Authentication auth) {
        User u = userServices.mustGetByUsername(auth.getName());

        if (!model.containsAttribute("profileForm")) {
            ProfileUpdateForm f = new ProfileUpdateForm();
            f.setFullName(u.getFullName());
            f.setUsername(u.getUsername());
            f.setEmail(u.getEmail());
            model.addAttribute("profileForm", f);
        }
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordChangeForm());
        }

        model.addAttribute("userEntity", u);
        return "admin/profile";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileUpdateForm form,
                                BindingResult binding,
                                Authentication auth,
                                RedirectAttributes ra) {
        if (binding.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.profileForm", binding);
            ra.addFlashAttribute("profileForm", form);
            ra.addFlashAttribute("error", "Please fix the highlighted errors.");
            return "redirect:/profile";
        }

        try {
            User current = userServices.mustGetByUsername(auth.getName());
            userServices.updateProfile(current, form.getFullName(), form.getUsername(), form.getEmail());
            ra.addFlashAttribute("success", "Profile updated successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("profileForm", form);
        }

        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 BindingResult binding,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        if (binding.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.passwordForm", binding);
            ra.addFlashAttribute("passwordForm", form);
            ra.addFlashAttribute("error", "Please fix the highlighted errors.");
            return "redirect:/profile";
        }
        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            ra.addFlashAttribute("error", "New passwords do not match.");
            ra.addFlashAttribute("passwordForm", form);
            return "redirect:/profile";
        }

        try {
            User current = userServices.mustGetByUsername(auth.getName());
            userServices.changePassword(current, form.getCurrentPassword(), form.getNewPassword());
            ra.addFlashAttribute("success", "Password changed successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("passwordForm", form);
        }

        return "redirect:/profile";
    }
}

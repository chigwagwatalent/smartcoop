package com.chicken.system.controller;

import com.chicken.system.dto.RegisterRequest;
import com.chicken.system.entity.User;
import com.chicken.system.enums.Role;
import com.chicken.system.services.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserServices userServices;

    public AuthController(UserServices userServices) {
        this.userServices = userServices;
    }

    @InitBinder("form")
    public void initTrim(WebDataBinder binder) {
        // prevent "spaces" from passing @NotBlank (optional but handy)
        binder.registerCustomEditor(String.class, new org.springframework.beans.propertyeditors.StringTrimmerEditor(true));
    }

    @ModelAttribute("form")
    public RegisterRequest form() {
        return new RegisterRequest("", "", "", "", "", false);
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(
            @Validated(RegisterRequest.Ordered.class) @ModelAttribute("form") RegisterRequest form,
            BindingResult result) {

        if (result.hasErrors()) return "auth/register";

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
            return "auth/register";
        }

        try {
            User u = new User();
            u.setFullName(form.getFullName());
            u.setUsername(form.getUsername());
            u.setEmail(form.getEmail());
            u.setRole(form.isAdmin() ? Role.ADMIN : Role.USER);

            userServices.registerUser(u, form.getPassword());
        } catch (IllegalArgumentException ex) {
            result.reject("registration_error", ex.getMessage());
            return "auth/register";
        }

        return "redirect:/auth/login?registered";
    }
}

package com.chicken.system.controller;

import com.chicken.system.entity.User;
import com.chicken.system.enums.Role;
import com.chicken.system.services.UserServices;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserServices userServices;

    public AuthController(UserServices userServices) {
        this.userServices = userServices;
    }

    // Provide a default form object for both GET and failed POST renders
    @ModelAttribute("form")
    public RegisterRequest form() {
        return new RegisterRequest("", "", "", "", "", false);
    }

    // --- LOGIN ---
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // --- REGISTER ---
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") RegisterRequest form,
                             BindingResult result) {

        if (result.hasErrors()) return "auth/register";

        if (!form.password().equals(form.confirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
            return "auth/register";
        }

        try {
            User u = new User();
            u.setFullName(form.fullName());
            u.setUsername(form.username());
            u.setEmail(form.email());
            u.setRole(form.isAdmin() ? Role.ADMIN : Role.USER);

            userServices.registerUser(u, form.password());
        } catch (IllegalArgumentException ex) {
            result.reject("registration_error", ex.getMessage());
            return "auth/register";
        }

        return "redirect:/auth/login?registered";
    }

    // --- DTO for registration ---
    public record RegisterRequest(
            @Size(max = 150) String fullName,
            @NotBlank @Size(min = 3, max = 60) String username,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank @Size(min = 8, max = 100) String confirmPassword,
            boolean isAdmin
    ) {}
}

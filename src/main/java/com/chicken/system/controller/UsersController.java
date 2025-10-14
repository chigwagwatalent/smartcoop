package com.chicken.system.controller;

import com.chicken.system.dto.UserForm;
import com.chicken.system.entity.User;
import com.chicken.system.enums.Role;
import com.chicken.system.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserServices service;

    public UsersController(UserServices service) {
        this.service = service;
    }

    @GetMapping
    public String page(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> p = service.page(pageable);

        model.addAttribute("page", p);
        model.addAttribute("pageIndex", page);
        model.addAttribute("size", size);
        model.addAttribute("roles", Role.values());

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UserForm());
        }
        if (!model.containsAttribute("openModal")) {
            model.addAttribute("openModal", false);
        }
        if (!model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", null);
        }
        return "admin/users";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") UserForm form,
                       BindingResult binding,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        // validate password rules
        boolean creating = (form.getId() == null);
        String pwd = form.getPassword() == null ? "" : form.getPassword();
        String conf = form.getConfirmPassword() == null ? "" : form.getConfirmPassword();

        if (creating) {
            if (pwd.isBlank() || pwd.length() < 8) {
                binding.rejectValue("password", "length", "Password must be at least 8 characters");
            }
            if (!pwd.equals(conf)) {
                binding.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
            }
        } else {
            if (!pwd.isBlank()) {
                if (pwd.length() < 8) {
                    binding.rejectValue("password", "length", "Password must be at least 8 characters");
                }
                if (!pwd.equals(conf)) {
                    binding.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
                }
            }
        }

        if (binding.hasErrors()) {
            model.addAttribute("openModal", true);
            model.addAttribute("roles", Role.values());
            return page(page, size, model);
        }

        try {
            if (creating) {
                User u = mapToEntity(form);
                service.create(u, pwd);
            } else {
                User incoming = mapToEntity(form);
                service.update(form.getId(), incoming, pwd.isBlank() ? null : pwd)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
            }
            return "redirect:/users?page=" + page + "&size=" + size;
        } catch (IllegalArgumentException ex) {
            binding.reject("business", ex.getMessage());
            model.addAttribute("openModal", true);
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return page(page, size, model);
        }
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size) {
        service.delete(id);
        return "redirect:/users?page=" + page + "&size=" + size;
    }

    private static User mapToEntity(UserForm f) {
        User u = new User();
        u.setId(f.getId());
        u.setFullName(f.getFullName());
        u.setUsername(f.getUsername());
        u.setEmail(f.getEmail());
        u.setRole(f.getRole());
        u.setEnabled(f.isEnabled());
        u.setLocked(f.isLocked());
        return u;
    }
}

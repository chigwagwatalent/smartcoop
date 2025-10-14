package com.chicken.system.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Map;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public Map<String, String> currentUser(Principal principal) {
        String name = (principal != null && principal.getName() != null) ? principal.getName() : "User";
        String initial = name.substring(0, 1).toUpperCase();
        return Map.of("name", name, "initial", initial);
    }
}

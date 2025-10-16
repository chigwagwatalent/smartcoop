package com.chicken.system.restcontroller;

import com.chicken.system.dto.LoginRequest;
import com.chicken.system.dto.UserProfileResponse;
import com.chicken.system.entity.User;
import com.chicken.system.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api/auth")
public class AuthRestController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Optional<User> byUsername = userRepository.findByUsername(req.getUsernameOrEmail());
        Optional<User> byEmail = byUsername.isPresent() ? byUsername : userRepository.findByEmail(req.getUsernameOrEmail());
        if (byEmail.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

        User u = byEmail.get();
        if (!u.isEnabled() || u.isLocked()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account disabled or locked");
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

        UserProfileResponse p = new UserProfileResponse();
        p.setId(u.getId());
        p.setFullName(u.getFullName());
        p.setUsername(u.getUsername());
        p.setEmail(u.getEmail());
        p.setRole(u.getRole());
        p.setEnabled(u.isEnabled());
        p.setLocked(u.isLocked());
        p.setCreatedAt(u.getCreatedAt());
        p.setUpdatedAt(u.getUpdatedAt());
        return ResponseEntity.ok(p);
    }
}

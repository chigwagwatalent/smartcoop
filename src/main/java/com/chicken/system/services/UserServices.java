package com.chicken.system.services;

import com.chicken.system.entity.User;
import com.chicken.system.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

@Service
public class UserServices implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServices(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /** Create a new user with a BCrypt-hashed password. */
    @Transactional
    public User registerUser(User u, String rawPassword) {
        if (userRepository.existsByUsername(u.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(u.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setEnabled(true);
        u.setLocked(false);
        return userRepository.save(u);
    }

    /** Spring Security uses this to authenticate during login. */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!u.isEnabled()) {
            throw new DisabledException("Account disabled");
        }
        if (u.isLocked()) {
            throw new LockedException("Account locked");
        }

        // Map your Role enum to authorities (e.g., ROLE_ADMIN)
        String roleName = "ROLE_" + u.getRole().name();
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(roleName))
        );
    }
}

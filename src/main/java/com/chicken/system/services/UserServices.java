package com.chicken.system.services;

import com.chicken.system.entity.User;
import com.chicken.system.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServices implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServices(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    // -------------------------
    // ORIGINAL METHOD: keep as-is
    // -------------------------
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

    // -------------------------
    // ORIGINAL METHOD: keep as-is
    // -------------------------
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!u.isEnabled()) throw new DisabledException("Account disabled");
        if (u.isLocked()) throw new LockedException("Account locked");

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
        );
    }

    // =================================================================
    // ADDITIONS for MVC CRUD (used by the Thymeleaf controller/pages)
    // =================================================================

    /** Page through users (e.g., for /users listing). */
    @Transactional(readOnly = true)
    public Page<User> page(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /** Find one by id (for edit form). */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /** Create a user (password required). Equivalent to registerUser but without forcing enabled/locked. */
    @Transactional
    public User create(User u, String rawPassword) {
        if (userRepository.existsByUsername(u.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(u.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        u.setPasswordHash(encoder.encode(rawPassword));
        return userRepository.save(u);
    }

    /**
     * Update a user. If rawPassword is non-null and not blank, password will be changed.
     * Also enforces username/email uniqueness (excluding the current user).
     */
    @Transactional
    public Optional<User> update(Long id, User incoming, String rawPasswordOpt) {
        return userRepository.findById(id).map(db -> {
            // uniqueness checks excluding current id
            if (userRepository.existsByUsernameAndIdNot(incoming.getUsername(), id)) {
                throw new IllegalArgumentException("Username already taken");
            }
            if (userRepository.existsByEmailAndIdNot(incoming.getEmail(), id)) {
                throw new IllegalArgumentException("Email already in use");
            }

            db.setFullName(incoming.getFullName());
            db.setUsername(incoming.getUsername());
            db.setEmail(incoming.getEmail());
            db.setRole(incoming.getRole());
            db.setEnabled(incoming.isEnabled());
            db.setLocked(incoming.isLocked());

            if (rawPasswordOpt != null && !rawPasswordOpt.isBlank()) {
                db.setPasswordHash(encoder.encode(rawPasswordOpt));
            }

            return userRepository.save(db);
        });
    }

    /** Delete a user by id. Returns true if deleted, false if not found. */
    @Transactional
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
}

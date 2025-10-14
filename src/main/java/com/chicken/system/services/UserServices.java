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

    @Transactional(readOnly = true)
    public Page<User> page(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

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

    @Transactional
    public Optional<User> update(Long id, User incoming, String rawPasswordOpt) {
        return userRepository.findById(id).map(db -> {
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

    @Transactional
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public User mustGetByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public User updateProfile(User current, String fullName, String username, String email) {
        if (!current.getUsername().equalsIgnoreCase(username)
                && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (!current.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        current.setFullName(fullName);
        current.setUsername(username);
        current.setEmail(email);
        return userRepository.save(current);
    }

    @Transactional
    public void changePassword(User current, String currentPassword, String newPassword) {
        if (!encoder.matches(currentPassword, current.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        current.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(current);
    }
}

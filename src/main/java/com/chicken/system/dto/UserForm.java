package com.chicken.system.dto;

import com.chicken.system.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserForm {
    private Long id;

    @Size(max = 150)
    private String fullName;

    @NotBlank @Size(min = 3, max = 60)
    private String username;

    @NotBlank @Email @Size(max = 160)
    private String email;

    // Password handling:
    // - On create (id == null): required & min 8
    // - On edit: optional; if provided, must be min 8 and match confirmPassword
    @Size(min = 0, max = 100)
    private String password;

    private String confirmPassword;

    private Role role = Role.USER;

    private boolean enabled = true;
    private boolean locked  = false;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}

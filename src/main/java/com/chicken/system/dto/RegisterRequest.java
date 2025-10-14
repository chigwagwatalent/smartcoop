package com.chicken.system.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    public interface Basic {}
    public interface SizeCheck {}

    @GroupSequence({Basic.class, SizeCheck.class, RegisterRequest.class})
    public interface Ordered {}

    @Size(max = 150, groups = SizeCheck.class)
    private String fullName;

    @NotBlank(groups = Basic.class)
    @Size(min = 3, max = 60, groups = SizeCheck.class)
    private String username;

    @NotBlank(groups = Basic.class)
    @Email(groups = Basic.class)
    @Size(max = 160, groups = SizeCheck.class)
    private String email;

    @NotBlank(groups = Basic.class)
    @Size(min = 8, max = 100, groups = SizeCheck.class)
    private String password;

    @NotBlank(groups = Basic.class)
    @Size(min = 8, max = 100, groups = SizeCheck.class)
    private String confirmPassword;

    private boolean admin;

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String username, String email,
                           String password, String confirmPassword, boolean admin) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.admin = admin;
    }

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

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
}

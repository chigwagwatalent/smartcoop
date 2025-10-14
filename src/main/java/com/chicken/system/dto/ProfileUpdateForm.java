// src/main/java/com/chicken/system/dto/ProfileUpdateForm.java
package com.chicken.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateForm {

    @Size(max = 150)
    private String fullName;

    @NotBlank @Size(min = 3, max = 60)
    private String username;

    @NotBlank @Email @Size(max = 160)
    private String email;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

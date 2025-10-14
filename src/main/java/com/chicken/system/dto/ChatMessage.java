// src/main/java/com/chicken/system/web/dto/ChatMessage.java
package com.chicken.system.dto;

public class ChatMessage {
    private String role;    // "user" | "assistant" (optional)
    private String content; // message text

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

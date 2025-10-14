// src/main/java/com/chicken/system/web/dto/AskRequest.java
package com.chicken.system.dto;

import java.util.List;

public class AskRequest {
    private List<ChatMessage> messages;

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}

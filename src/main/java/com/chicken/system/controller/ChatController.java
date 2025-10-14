package com.chicken.system.controller;

import com.chicken.system.dto.AskRequest;
import com.chicken.system.dto.AskResponse;
import com.chicken.system.services.GeminiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final GeminiChatService gemini;

    public ChatController(GeminiChatService gemini) {
        this.gemini = gemini;
    }

    @GetMapping("/ai-chat")
    public String page() {
        return "admin/ai-chat";
    }

    @PostMapping("/ai-chat/ask")
    @ResponseBody
    public ResponseEntity<AskResponse> ask(@RequestBody AskRequest req) {
        // Collapse the chat history into a single prompt
        String prompt = (req.getMessages() == null ? "" :
                req.getMessages().stream()
                        .map(m -> {
                            String role = m.getRole() == null ? "user" : m.getRole();
                            String content = m.getContent() == null ? "" : m.getContent();
                            return role + ": " + content;
                        })
                        .collect(Collectors.joining("\n\n"))
        );

        String reply = gemini.ask(prompt);
        return ResponseEntity.ok(new AskResponse(reply));
    }
}

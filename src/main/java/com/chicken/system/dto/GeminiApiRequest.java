package com.chicken.system.dto;

import java.util.ArrayList;
import java.util.List;

/** Minimal request body for Gemini /generateContent */
public class GeminiApiRequest {

    public static class Part {
        public String text;
        public Part() {}
        public Part(String text) { this.text = text; }
    }

    public static class Content {
        public String role; // "user" or "model"
        public List<Part> parts = new ArrayList<>();
    }

    public List<Content> contents = new ArrayList<>();

    public static GeminiApiRequest from(List<ChatMessage> messages) {
        GeminiApiRequest req = new GeminiApiRequest();
        if (messages == null) return req;

        for (ChatMessage m : messages) {
            if (m == null) continue;
            Content c = new Content();

            // Gemini expects "user" or "model"
            String role = (m.getRole() == null) ? "user" : m.getRole().trim().toLowerCase();
            c.role = "model".equals(role) ? "model" : "user";

            String text = (m.getContent() == null) ? "" : m.getContent();
            c.parts.add(new Part(text));

            req.contents.add(c);
        }
        return req;
    }
}

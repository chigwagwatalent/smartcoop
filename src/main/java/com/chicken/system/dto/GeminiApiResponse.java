package com.chicken.system.dto;

import java.util.List;

/** Minimal shape of Gemini REST response for text outputs. */
public class GeminiApiResponse {

    public List<Candidate> candidates;

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }

    /** Helper to grab the first text answer safely. */
    public static String extractText(GeminiApiResponse r) {
        if (r == null || r.candidates == null || r.candidates.isEmpty()) return null;
        Content c = r.candidates.get(0).content;
        if (c == null || c.parts == null || c.parts.isEmpty()) return null;
        return c.parts.get(0).text;
    }
}

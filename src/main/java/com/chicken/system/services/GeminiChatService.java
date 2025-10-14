package com.chicken.system.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiChatService {

    private final RestClient http;
    private final String apiKey;
    private final String base;
    private final String version;
    private final String model;

    private final double temperature;
    private final Integer topK;
    private final Double topP;

    public GeminiChatService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.base}") String base,
            @Value("${gemini.api.version}") String version,
            @Value("${gemini.model}") String model,
            @Value("${gemini.temperature:0.3}") double temperature,
            @Value("${gemini.topK:32}") Integer topK,
            @Value("${gemini.topP:0.95}") Double topP
    ) {
        this.apiKey = apiKey;
        this.base = trimEnd(base);
        this.version = trimSlashes(version);
        this.model = model;
        this.temperature = temperature;
        this.topK = topK;
        this.topP = topP;

        this.http = RestClient.builder()
                .baseUrl(this.base)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** Public ask that accepts a single prompt string. */
    public String ask(String prompt) {
        if (prompt == null) prompt = "";
        GenerateRequest req = GenerateRequest.singleUserText(prompt, temperature, topK, topP);
        GenerateResponse res = send(req);
        String text = res != null ? res.firstTextOrNull() : null;
        return (text == null || text.isBlank()) ? "(no response)" : text.trim();
    }

    /** POST to /v{version}/models/{model}:generateContent?key=... */
    private GenerateResponse send(GenerateRequest body) {
        String path = String.format("/%s/models/%s:generateContent?key=%s", version, model, apiKey);
        return http.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(GenerateResponse.class);
    }

    private static String trimEnd(String s){
        if (s == null) return "";
        return s.endsWith("/") ? s.substring(0, s.length()-1) : s;
    }
    private static String trimSlashes(String s){
        if (s == null) return "";
        String t = s;
        while (t.startsWith("/")) t = t.substring(1);
        while (t.endsWith("/")) t = t.substring(0, t.length()-1);
        return t;
    }

    /* ---------- Minimal DTOs for generateContent ---------- */

    record GenerateRequest(
            List<Content> contents,
            @JsonProperty("generationConfig") Map<String,Object> generationConfig
    ){
        static GenerateRequest singleUserText(String text, double temperature, Integer topK, Double topP){
            return new GenerateRequest(
                    List.of(new Content("user", List.of(new Part(text)))),
                    Map.of(
                            "temperature", temperature,
                            "topK", topK,
                            "topP", topP
                    )
            );
        }
    }
    record Content(String role, List<Part> parts){}
    record Part(String text){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GenerateResponse {
        public List<Candidate> candidates;
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Candidate {
            public ContentOut content;
        }
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class ContentOut {
            public List<PartOut> parts;
        }
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class PartOut { public String text; }

        String firstTextOrNull(){
            if (candidates == null || candidates.isEmpty()) return null;
            ContentOut c = candidates.get(0).content;
            if (c == null || c.parts == null || c.parts.isEmpty()) return null;
            return c.parts.get(0).text;
        }
    }
}

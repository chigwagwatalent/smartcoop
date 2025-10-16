package com.chicken.system.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GeminiChatService {

    private final RestClient http;
    private final String apiKey;
    private final String base;     // e.g. https://generativelanguage.googleapis.com
    private final String version;  // v1
    private final String preferredModel; // from properties

    private final double temperature;
    private final Integer topK;
    private final Double topP;

    // cache discovered usable models (names that support generateContent)  //TODO: 
    private final AtomicReference<List<String>> usableModels = new AtomicReference<>();

    public GeminiChatService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.base}") String base,
            @Value("${gemini.api.version}") String version,
            @Value("${gemini.model}") String model,
            @Value("${gemini.temperature:0.3}") double temperature,
            @Value("${gemini.topK:32}") Integer topK,
            @Value("${gemini.topP:0.95}") Double topP
    ) {
        this.apiKey = Objects.requireNonNull(apiKey, "gemini.api.key");
        this.base = trimEnd(base);
        this.version = trimSlashes(version);
        this.preferredModel = model;
        this.temperature = temperature;
        this.topK = topK;
        this.topP = topP;

        this.http = RestClient.builder()
                .baseUrl(this.base)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String ask(String prompt) {
        if (prompt == null) prompt = "";

        List<String> candidates = ensureUsableModels();

        if (candidates.isEmpty()) {
            return "(No Gemini models with generateContent are available for this API key/project on "
                    + version + ". Enable Generative Language API for your key, then retry.)";
        }

        List<String> tryOrder = new ArrayList<>();
        if (preferredModel != null && !preferredModel.isBlank()) {
            tryOrder.add(preferredModel);
        }
        for (String m : candidates) if (!tryOrder.contains(m)) tryOrder.add(m);

        for (String modelName : tryOrder) {
            try {
                GenerateRequest req = GenerateRequest.singleUserText(prompt, temperature, topK, topP);
                GenerateResponse res = send(req, modelName);
                String text = (res == null) ? null : res.firstTextOrNull();
                if (text != null && !text.isBlank()) return text.trim();
            } catch (HttpClientErrorException.NotFound nf) {
            } catch (HttpClientErrorException e) {
                return "(Gemini error " + e.getStatusCode().value() + " on model " + modelName + "): "
                        + safeBody(e.getResponseBodyAsString());
            }
        }
        return "(All candidate models returned empty or errors. Check API enablement/quota and try again.)";
    }

    private List<String> ensureUsableModels() {
        List<String> current = usableModels.get();
        if (current != null) return current;
        try {
            String path = String.format("/%s/models?key=%s", version, apiKey);
            ModelList list = http.get().uri(path).retrieve().body(ModelList.class);
            List<String> names = new ArrayList<>();
            if (list != null && list.models != null) {
                for (ModelInfo m : list.models) {
                    if (m.supportedGenerationMethods != null &&
                        m.supportedGenerationMethods.contains("generateContent")) {
                        names.add(m.name.replace("models/",""));
                    }
                }
            }
            // sort to prefer flash/pro 1.5 first
            names.sort(Comparator.comparing((String n) ->
                    n.contains("1.5-flash") ? 0 : n.contains("1.5-pro") ? 1 : 2)
                    .thenComparing(Comparator.naturalOrder()));
            usableModels.compareAndSet(null, names);
            return names;
        } catch (HttpClientErrorException e) {
            // If list call fails, keep empty to show helpful message in ask()
            usableModels.compareAndSet(null, List.of());
            return List.of();
        }
    }

    /** POST {base}/{version}/models/{model}:generateContent?key=... */
    private GenerateResponse send(GenerateRequest body, String model) {
        String path = String.format("/%s/models/%s:generateContent?key=%s", version, model, apiKey);
        return http.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(GenerateResponse.class);
    }

    private static String safeBody(String s){
        if (s == null || s.isBlank()) return "(no body)";
        // short & clean for UI
        return s.length() > 500 ? s.substring(0, 500) + "..." : s;
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

    /* ---------- DTOs ---------- */

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
        static class Candidate { public ContentOut content; }
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class ContentOut { public List<PartOut> parts; }
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class PartOut { public String text; }

        String firstTextOrNull(){
            if (candidates == null || candidates.isEmpty()) return null;
            ContentOut c = candidates.get(0).content;
            if (c == null || c.parts == null || c.parts.isEmpty()) return null;
            return c.parts.get(0).text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ModelList {
        public List<ModelInfo> models;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ModelInfo {
        public String name; // e.g. models/gemini-1.5-flash
        public List<String> supportedGenerationMethods; // e.g. ["generateContent"]
    }
}

package com.chicken.system.controller;

import com.chicken.system.entity.Actuator;
import com.chicken.system.services.ActuatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class ActuatorController {

    private final ActuatorService service;

    public ActuatorController(ActuatorService service) {
        this.service = service;
    }

    /** Page */
    @GetMapping("/actuators")
    public String page(Model model) {
        Actuator a = service.current();
        model.addAttribute("state", Map.of(
                "pumpOn", a.isPumpOn(),
                "fanOn",  a.isFanOn(),
                "updatedAt", a.getUpdatedAt()
        ));
        return "admin/actuators";
    }

    /** JSON: current state (for polling) */
    @GetMapping("/actuators/state")
    @ResponseBody
    public Map<String, Object> state() {
        Actuator a = service.current();
        return Map.of(
                "pumpOn", a.isPumpOn(),
                "fanOn",  a.isFanOn(),
                "updatedAt", String.valueOf(a.getUpdatedAt())
        );
    }

    /** Actions (MVC endpoints returning JSON for the page’s JS) */
    @PostMapping("/actuators/pump/{action}")
    @ResponseBody
    public ResponseEntity<?> pump(@PathVariable String action) {
        Actuator a;
        switch (action.toLowerCase()) {
            case "on"     -> a = service.setPump(true);
            case "off"    -> a = service.setPump(false);
            case "toggle" -> a = service.togglePump();
            default       -> { return ResponseEntity.badRequest().body(Map.of("message","invalid action")); }
        }
        return ResponseEntity.ok(Map.of("pumpOn", a.isPumpOn(), "fanOn", a.isFanOn()));
    }

    @PostMapping("/actuators/fan/{action}")
    @ResponseBody
    public ResponseEntity<?> fan(@PathVariable String action) {
        Actuator a;
        switch (action.toLowerCase()) {
            case "on"     -> a = service.setFan(true);
            case "off"    -> a = service.setFan(false);
            case "toggle" -> a = service.toggleFan();
            default       -> { return ResponseEntity.badRequest().body(Map.of("message","invalid action")); }
        }
        return ResponseEntity.ok(Map.of("pumpOn", a.isPumpOn(), "fanOn", a.isFanOn()));
    }
}

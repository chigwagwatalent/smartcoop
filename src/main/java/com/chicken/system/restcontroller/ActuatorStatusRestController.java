package com.chicken.system.restcontroller;

import com.chicken.system.dto.ActuatorStatusNumericResponse;
import com.chicken.system.services.ActuatorStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actuator")
public class ActuatorStatusRestController {

    private final ActuatorStatusService service;

    public ActuatorStatusRestController(ActuatorStatusService service) {
        this.service = service;
    }

    /** GET /api/actuator/status -> { "pumpOn": 1|0, "fanOn": 1|0 } */
    @GetMapping(value = "/status", produces = "application/json")
    public ResponseEntity<ActuatorStatusNumericResponse> getStatus() {
        return ResponseEntity.ok(service.getStatusNumeric());
    }
}

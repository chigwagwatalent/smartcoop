package com.chicken.system.services;

import com.chicken.system.dto.ActuatorStatusNumericResponse;
import com.chicken.system.entity.Actuator;
import com.chicken.system.repository.ActuatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActuatorStatusService {

    private final ActuatorRepository repository;

    public ActuatorStatusService(ActuatorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ActuatorStatusNumericResponse getStatusNumeric() {
        return repository.findTop1ByOrderByUpdatedAtDesc()
                .map(this::toNumeric)
                .orElseGet(() -> new ActuatorStatusNumericResponse(0, 0));
    }

    private ActuatorStatusNumericResponse toNumeric(Actuator a) {
        int pump = a.isPumpOn() ? 1 : 0;
        int fan  = a.isFanOn()  ? 1 : 0;
        return new ActuatorStatusNumericResponse(pump, fan);
    }
}

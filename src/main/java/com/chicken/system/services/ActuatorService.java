package com.chicken.system.services;

import com.chicken.system.entity.Actuator;
import com.chicken.system.repository.ActuatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActuatorService {

    private final ActuatorRepository repo;

    public ActuatorService(ActuatorRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Actuator current() {
        return repo.findTop1ByOrderByUpdatedAtDesc()
                .orElseGet(() -> repo.save(new Actuator()));
    }

    @Transactional
    public Actuator setPump(boolean on) {
        Actuator a = current();
        a.setPumpOn(on);
        return repo.save(a);
    }

    @Transactional
    public Actuator setFan(boolean on) {
        Actuator a = current();
        a.setFanOn(on);
        return repo.save(a);
    }

    @Transactional
    public Actuator togglePump() {
        Actuator a = current();
        a.setPumpOn(!a.isPumpOn());
        return repo.save(a);
    }

    @Transactional
    public Actuator toggleFan() {
        Actuator a = current();
        a.setFanOn(!a.isFanOn());
        return repo.save(a);
    }
}

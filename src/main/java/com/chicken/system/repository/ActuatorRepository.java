package com.chicken.system.repository;

import com.chicken.system.entity.Actuator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActuatorRepository extends JpaRepository<Actuator, Long> {
    Optional<Actuator> findTop1ByOrderByUpdatedAtDesc();
    
}

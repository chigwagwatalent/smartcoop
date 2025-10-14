package com.chicken.system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "actuator_status", indexes = {
        @Index(name = "idx_actuator_status_created", columnList = "created_at")
})
public class Actuator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 1 = ON, 0 = OFF */
    @Column(name = "pump_on", nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean pumpOn = false;

    /** 1 = ON, 0 = OFF */
    @Column(name = "fan_on", nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean fanOn = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Getters & Setters

    public Long getId() { return id; }

    public boolean isPumpOn() { return pumpOn; }
    public void setPumpOn(boolean pumpOn) { this.pumpOn = pumpOn; }

    public boolean isFanOn() { return fanOn; }
    public void setFanOn(boolean fanOn) { this.fanOn = fanOn; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

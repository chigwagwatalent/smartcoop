package com.chicken.system.dto;

import java.time.LocalDateTime;

public class ActuatorStateDto {
    private boolean pumpOn;
    private boolean fanOn;
    private boolean servoOn;
    private LocalDateTime updatedAt;

    public ActuatorStateDto(boolean pumpOn, boolean fanOn, boolean servoOn, LocalDateTime updatedAt) {
        this.pumpOn = pumpOn;
        this.fanOn = fanOn;
        this.servoOn = servoOn;
        this.updatedAt = updatedAt;
    }

    public boolean isPumpOn() { return pumpOn; }
    public boolean isFanOn() { return fanOn; }
    public boolean isServoOn() { return servoOn; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

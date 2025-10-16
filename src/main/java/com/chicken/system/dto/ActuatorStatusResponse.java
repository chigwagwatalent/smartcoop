package com.chicken.system.dto;

import java.time.LocalDateTime;

public class ActuatorStatusResponse {
    private boolean pumpOn;
    private boolean fanOn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isPumpOn() { return pumpOn; }
    public void setPumpOn(boolean pumpOn) { this.pumpOn = pumpOn; }
    public boolean isFanOn() { return fanOn; }
    public void setFanOn(boolean fanOn) { this.fanOn = fanOn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

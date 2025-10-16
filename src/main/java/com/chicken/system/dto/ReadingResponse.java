package com.chicken.system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReadingResponse {
    private Long id;
    private BigDecimal temperatureC;
    private BigDecimal humidityPct;
    private BigDecimal waterLevelPct;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getTemperatureC() { return temperatureC; }
    public void setTemperatureC(BigDecimal temperatureC) { this.temperatureC = temperatureC; }
    public BigDecimal getHumidityPct() { return humidityPct; }
    public void setHumidityPct(BigDecimal humidityPct) { this.humidityPct = humidityPct; }
    public BigDecimal getWaterLevelPct() { return waterLevelPct; }
    public void setWaterLevelPct(BigDecimal waterLevelPct) { this.waterLevelPct = waterLevelPct; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

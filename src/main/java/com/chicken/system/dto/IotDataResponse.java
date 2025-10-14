package com.chicken.system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IotDataResponse {
    private Long id;
    private BigDecimal temperatureC;
    private BigDecimal humidityPct;
    private BigDecimal waterLevelPct;
    private LocalDateTime createdAt;

    public IotDataResponse(Long id, BigDecimal temperatureC, BigDecimal humidityPct,
                           BigDecimal waterLevelPct, LocalDateTime createdAt) {
        this.id = id;
        this.temperatureC = temperatureC;
        this.humidityPct = humidityPct;
        this.waterLevelPct = waterLevelPct;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public BigDecimal getTemperatureC() { return temperatureC; }
    public BigDecimal getHumidityPct() { return humidityPct; }
    public BigDecimal getWaterLevelPct() { return waterLevelPct; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

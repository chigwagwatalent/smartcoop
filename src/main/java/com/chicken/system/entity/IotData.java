package com.chicken.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "iot_data", indexes = {
        @Index(name = "idx_iot_data_created_at", columnList = "created_at")
})
public class IotData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "temperature_c", precision = 6, scale = 2, nullable = false)
    private BigDecimal temperatureC;

    @NotNull @Min(0) @Max(100)
    @Column(name = "humidity_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal humidityPct;

    @NotNull @Min(0) @Max(100)
    @Column(name = "water_level_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal waterLevelPct;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }

    public BigDecimal getTemperatureC() { return temperatureC; }
    public void setTemperatureC(BigDecimal temperatureC) { this.temperatureC = temperatureC; }

    public BigDecimal getHumidityPct() { return humidityPct; }
    public void setHumidityPct(BigDecimal humidityPct) { this.humidityPct = humidityPct; }

    public BigDecimal getWaterLevelPct() { return waterLevelPct; }
    public void setWaterLevelPct(BigDecimal waterLevelPct) { this.waterLevelPct = waterLevelPct; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

package com.chicken.system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DashboardSummaryResponse {
    private BigDecimal temperatureC;
    private BigDecimal humidityPct;
    private BigDecimal waterLevelPct;

    private boolean fanOn;
    private boolean pumpOn;

    private Integer chicksCount;      // latest count if available
    private LocalDateTime readingsAt; // timestamp of latest reading

    public BigDecimal getTemperatureC() { return temperatureC; }
    public void setTemperatureC(BigDecimal temperatureC) { this.temperatureC = temperatureC; }
    public BigDecimal getHumidityPct() { return humidityPct; }
    public void setHumidityPct(BigDecimal humidityPct) { this.humidityPct = humidityPct; }
    public BigDecimal getWaterLevelPct() { return waterLevelPct; }
    public void setWaterLevelPct(BigDecimal waterLevelPct) { this.waterLevelPct = waterLevelPct; }
    public boolean isFanOn() { return fanOn; }
    public void setFanOn(boolean fanOn) { this.fanOn = fanOn; }
    public boolean isPumpOn() { return pumpOn; }
    public void setPumpOn(boolean pumpOn) { this.pumpOn = pumpOn; }
    public Integer getChicksCount() { return chicksCount; }
    public void setChicksCount(Integer chicksCount) { this.chicksCount = chicksCount; }
    public LocalDateTime getReadingsAt() { return readingsAt; }
    public void setReadingsAt(LocalDateTime readingsAt) { this.readingsAt = readingsAt; }
}

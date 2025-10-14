package com.chicken.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateIotDataRequest {

    @NotNull
    private BigDecimal temperatureC;

    @NotNull @Min(0) @Max(100)
    private BigDecimal humidityPct;

    @NotNull @Min(0) @Max(100)
    private BigDecimal waterLevelPct;

    public BigDecimal getTemperatureC() { return temperatureC; }
    public void setTemperatureC(BigDecimal temperatureC) { this.temperatureC = temperatureC; }

    public BigDecimal getHumidityPct() { return humidityPct; }
    public void setHumidityPct(BigDecimal humidityPct) { this.humidityPct = humidityPct; }

    public BigDecimal getWaterLevelPct() { return waterLevelPct; }
    public void setWaterLevelPct(BigDecimal waterLevelPct) { this.waterLevelPct = waterLevelPct; }
}

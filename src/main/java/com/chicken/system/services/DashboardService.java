package com.chicken.system.services;

import com.chicken.system.entity.IotData;
import com.chicken.system.repository.ActuatorRepository;
import com.chicken.system.repository.IotDataRepository;
import com.chicken.system.repository.StockDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DashboardService {

    private final IotDataRepository iotDataRepository;
    private final StockDataRepository stockDataRepository;
    private final ActuatorRepository actuatorRepository;

    public DashboardService(IotDataRepository iotDataRepository,
                            StockDataRepository stockDataRepository,
                            ActuatorRepository actuatorRepository) {
        this.iotDataRepository = iotDataRepository;
        this.stockDataRepository = stockDataRepository;
        this.actuatorRepository = actuatorRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSummary() {
        IotData latest = iotDataRepository.findTop1ByOrderByCreatedAtDesc().orElse(null);
        var latestTemp = latest != null ? latest.getTemperatureC() : BigDecimal.ZERO;
        var latestHum  = latest != null ? latest.getHumidityPct()  : BigDecimal.ZERO;
        var latestLevel= latest != null ? latest.getWaterLevelPct(): BigDecimal.ZERO;

        var latestStock = stockDataRepository.findTop1ByOrderByCreatedAtDesc().orElse(null);
        var chicksCount = latestStock != null ? latestStock.getChicksCount() : 0;

        var actuator = actuatorRepository.findTop1ByOrderByUpdatedAtDesc();
        var pumpOn = actuator.map(a -> a.isPumpOn() ? 1 : 0).orElse(0);
        var fanOn  = actuator.map(a -> a.isFanOn()  ? 1 : 0).orElse(0);

        return Map.of(
                "temperatureC", latestTemp,
                "humidityPct", latestHum,
                "waterLevelPct", latestLevel,
                "chicksCount", chicksCount,
                "pumpOn", pumpOn,
                "fanOn", fanOn
        );
    }
}

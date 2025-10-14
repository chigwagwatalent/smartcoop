package com.chicken.system.restcontroller;

import com.chicken.system.entity.IotData;
import com.chicken.system.entity.StockData;
import com.chicken.system.repository.ActuatorRepository;
import com.chicken.system.repository.IotDataRepository;
import com.chicken.system.repository.StockDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final IotDataRepository iotRepo;
    private final StockDataRepository stockRepo;
    private final ActuatorRepository actuatorRepo;

    public DashboardRestController(IotDataRepository iotRepo,
                                   StockDataRepository stockRepo,
                                   ActuatorRepository actuatorRepo) {
        this.iotRepo = iotRepo;
        this.stockRepo = stockRepo;
        this.actuatorRepo = actuatorRepo;
    }

    /** Summary block for top tiles */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary() {
        var latestIot = iotRepo.findTop1ByOrderByCreatedAtDesc().orElse(null);
        var latestStock = stockRepo.findTop1ByOrderByCreatedAtDesc().orElse(null);
        var actuator = actuatorRepo.findTop1ByOrderByUpdatedAtDesc();

        Map<String, Object> payload = new HashMap<>();
        payload.put("temperatureC", latestIot != null ? latestIot.getTemperatureC() : 0);
        payload.put("humidityPct",  latestIot != null ? latestIot.getHumidityPct()  : 0);
        payload.put("waterLevelPct",latestIot != null ? latestIot.getWaterLevelPct(): 0);
        payload.put("chicksCount",  latestStock != null ? latestStock.getChicksCount() : 0);
        payload.put("pumpOn", actuator.map(a -> a.isPumpOn() ? 1 : 0).orElse(0));
        payload.put("fanOn",  actuator.map(a -> a.isFanOn()  ? 1 : 0).orElse(0));
        payload.put("timestamp", latestIot != null ? latestIot.getCreatedAt().toString() : null);
        return ResponseEntity.ok(payload);
    }

    /** Last 50 IoT readings for charts */
    @GetMapping("/iot/timeseries")
    public ResponseEntity<Map<String, Object>> iotTimeSeries() {
        List<IotData> last50 = iotRepo.findTop50ByOrderByCreatedAtDesc();
        // reverse chronological -> chronological
        Collections.reverse(last50);

        List<String> labels = new ArrayList<>();
        List<Number> temp = new ArrayList<>();
        List<Number> hum = new ArrayList<>();
        List<Number> level = new ArrayList<>();

        for (var r : last50) {
            labels.add(r.getCreatedAt().toString());
            temp.add(r.getTemperatureC());
            hum.add(r.getHumidityPct());
            level.add(r.getWaterLevelPct());
        }
        return ResponseEntity.ok(Map.of(
                "labels", labels,
                "temperatureC", temp,
                "humidityPct", hum,
                "waterLevelPct", level
        ));
    }

    /** Last 30 stock entries (chicks count) */
    @GetMapping("/stock/timeseries")
    public ResponseEntity<Map<String, Object>> stockTimeSeries() {
        List<StockData> last30 = stockRepo.findTop30ByOrderByCreatedAtDesc();
        Collections.reverse(last30);

        List<String> labels = new ArrayList<>();
        List<Number> chicks = new ArrayList<>();
        for (var s : last30) {
            labels.add(s.getCreatedAt().toString());
            chicks.add(s.getChicksCount());
        }
        return ResponseEntity.ok(Map.of("labels", labels, "chicksCount", chicks));
    }
}

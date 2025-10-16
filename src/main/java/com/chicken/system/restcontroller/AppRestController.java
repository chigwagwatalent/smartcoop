package com.chicken.system.restcontroller;

import com.chicken.system.dto.*;
import com.chicken.system.entity.Actuator;
import com.chicken.system.entity.IotData;
import com.chicken.system.entity.StockData;
import com.chicken.system.repository.ActuatorRepository;
import com.chicken.system.repository.IotDataRepository;
import com.chicken.system.repository.StockDataRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api")
public class AppRestController {

    private final IotDataRepository iotDataRepository;
    private final ActuatorRepository actuatorRepository;
    private final StockDataRepository stockDataRepository;

    @Autowired
    public AppRestController(IotDataRepository iotDataRepository,
                             ActuatorRepository actuatorRepository,
                             StockDataRepository stockDataRepository) {
        this.iotDataRepository = iotDataRepository;
        this.actuatorRepository = actuatorRepository;
        this.stockDataRepository = stockDataRepository;
    }

    /* -------------------- Readings -------------------- */

    // ESP32 posts telemetry here (payload matches your curl)
    @PostMapping("/iot/reading")
    @Transactional
    public ResponseEntity<ReadingResponse> createReading(@Valid @RequestBody ReadingCreateRequest req) {
        IotData d = new IotData();
        d.setTemperatureC(req.getTemperatureC());
        d.setHumidityPct(req.getHumidityPct());
        d.setWaterLevelPct(req.getWaterLevelPct());
        IotData saved = iotDataRepository.save(d);

        ReadingResponse out = new ReadingResponse();
        out.setId(saved.getId());
        out.setTemperatureC(saved.getTemperatureC());
        out.setHumidityPct(saved.getHumidityPct());
        out.setWaterLevelPct(saved.getWaterLevelPct());
        out.setCreatedAt(saved.getCreatedAt());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/iot/reading/latest")
    public ResponseEntity<ReadingResponse> latestReading() {
        Optional<IotData> opt = iotDataRepository.findTop1ByOrderByCreatedAtDesc();
        if (opt.isEmpty()) return ResponseEntity.noContent().build();

        IotData r = opt.get();
        ReadingResponse out = new ReadingResponse();
        out.setId(r.getId());
        out.setTemperatureC(r.getTemperatureC());
        out.setHumidityPct(r.getHumidityPct());
        out.setWaterLevelPct(r.getWaterLevelPct());
        out.setCreatedAt(r.getCreatedAt());
        return ResponseEntity.ok(out);
    }

    // Flutter can page through history (newest first)
    @GetMapping("/iot/reading")
    public ResponseEntity<Page<ReadingResponse>> listReadings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 200), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<IotData> pageData = iotDataRepository.findAllByOrderByCreatedAtDesc(pageable);

        Page<ReadingResponse> mapped = pageData.map(r -> {
            ReadingResponse out = new ReadingResponse();
            out.setId(r.getId());
            out.setTemperatureC(r.getTemperatureC());
            out.setHumidityPct(r.getHumidityPct());
            out.setWaterLevelPct(r.getWaterLevelPct());
            out.setCreatedAt(r.getCreatedAt());
            return out;
        });
        return ResponseEntity.ok(mapped);
    }

    /* -------------------- Actuators -------------------- */

    // Get current actuator state (latest row)
    @GetMapping("/actuators")
    public ResponseEntity<ActuatorStatusResponse> getActuators() {
        Optional<Actuator> opt = actuatorRepository.findTop1ByOrderByUpdatedAtDesc();
        if (opt.isEmpty()) return ResponseEntity.ok(emptyActuatorResponse());

        Actuator a = opt.get();
        ActuatorStatusResponse resp = new ActuatorStatusResponse();
        resp.setFanOn(a.isFanOn());
        resp.setPumpOn(a.isPumpOn());
        resp.setCreatedAt(a.getCreatedAt());
        resp.setUpdatedAt(a.getUpdatedAt());
        return ResponseEntity.ok(resp);
    }

    // Set FAN on/off
    @PostMapping("/actuators/fan")
    @Transactional
    public ResponseEntity<ActuatorStatusResponse> setFan(@Valid @RequestBody ActuatorCommandRequest req) {
        Actuator a = actuatorRepository.findTop1ByOrderByUpdatedAtDesc().orElseGet(Actuator::new);
        a.setFanOn(Boolean.TRUE.equals(req.getOn()));
        // keep pump as-is if exists; default false for new
        if (a.getId() == null) a.setPumpOn(false);
        Actuator saved = actuatorRepository.save(a);
        return ResponseEntity.ok(mapActuator(saved));
    }

    // Set PUMP on/off
    @PostMapping("/actuators/pump")
    @Transactional
    public ResponseEntity<ActuatorStatusResponse> setPump(@Valid @RequestBody ActuatorCommandRequest req) {
        Actuator a = actuatorRepository.findTop1ByOrderByUpdatedAtDesc().orElseGet(Actuator::new);
        a.setPumpOn(Boolean.TRUE.equals(req.getOn()));
        if (a.getId() == null) a.setFanOn(false);
        Actuator saved = actuatorRepository.save(a);
        return ResponseEntity.ok(mapActuator(saved));
    }

    /* -------------------- Dashboard Summary -------------------- */

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> dashboardSummary() {
        DashboardSummaryResponse out = new DashboardSummaryResponse();

        // latest readings
        iotDataRepository.findTop1ByOrderByCreatedAtDesc().ifPresent(r -> {
            out.setTemperatureC(safe(r.getTemperatureC()));
            out.setHumidityPct(safe(r.getHumidityPct()));
            out.setWaterLevelPct(safe(r.getWaterLevelPct()));
            out.setReadingsAt(r.getCreatedAt());
        });

        // latest actuators
        actuatorRepository.findTop1ByOrderByUpdatedAtDesc().ifPresentOrElse(a -> {
            out.setFanOn(a.isFanOn());
            out.setPumpOn(a.isPumpOn());
        }, () -> {
            out.setFanOn(false);
            out.setPumpOn(false);
        });

        // latest chicks count (if present)
        Optional<StockData> stock = stockDataRepository.findTop1ByOrderByCreatedAtDesc();
        stock.ifPresent(s -> out.setChicksCount(s.getChicksCount()));

        return ResponseEntity.ok(out);
    }

    /* -------------------- Helpers -------------------- */

    private static DashboardSummaryResponse defaults(DashboardSummaryResponse d) {
        d.setTemperatureC(null);
        d.setHumidityPct(null);
        d.setWaterLevelPct(null);
        d.setFanOn(false);
        d.setPumpOn(false);
        d.setChicksCount(null);
        d.setReadingsAt(null);
        return d;
    }

    private static ActuatorStatusResponse emptyActuatorResponse() {
        ActuatorStatusResponse resp = new ActuatorStatusResponse();
        resp.setFanOn(false);
        resp.setPumpOn(false);
        resp.setCreatedAt(null);
        resp.setUpdatedAt(null);
        return resp;
    }

    private static ActuatorStatusResponse mapActuator(Actuator a) {
        ActuatorStatusResponse resp = new ActuatorStatusResponse();
        resp.setFanOn(a.isFanOn());
        resp.setPumpOn(a.isPumpOn());
        resp.setCreatedAt(a.getCreatedAt());
        resp.setUpdatedAt(a.getUpdatedAt());
        return resp;
    }

    private static BigDecimal safe(BigDecimal v) { return v; }
}

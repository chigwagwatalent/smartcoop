package com.chicken.system.services;

import com.chicken.system.entity.IotData;
import com.chicken.system.repository.IotDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class IotDataViewService {

    private final IotDataRepository repo;
    private final IotStreamService stream;
    private final ObjectMapper mapper;

    public IotDataViewService(IotDataRepository repo, IotStreamService stream, ObjectMapper mapper) {
        this.repo = repo;
        this.stream = stream;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> latestSummary() {
        var latest = repo.findTop1ByOrderByCreatedAtDesc().orElse(null);

        // Defaults (avoid nulls in the map)
        BigDecimal temp  = BigDecimal.ZERO;
        BigDecimal hum   = BigDecimal.ZERO;
        BigDecimal level = BigDecimal.ZERO;
        String ts        = "";

        if (latest != null) {
            if (latest.getTemperatureC() != null) temp  = latest.getTemperatureC();
            if (latest.getHumidityPct()  != null) hum   = latest.getHumidityPct();
            if (latest.getWaterLevelPct()!= null) level = latest.getWaterLevelPct();
            if (latest.getCreatedAt()    != null) ts    = latest.getCreatedAt().toString();
        }

        Map<String, Object> out = new HashMap<>();
        out.put("temperatureC", temp);
        out.put("humidityPct",  hum);
        out.put("waterLevelPct",level);
        out.put("timestamp",    ts);
        return out;
    }

    @Transactional(readOnly = true)
    public Page<IotData> page(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);   // PageRequest implements Pageable
        return repo.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public void pushNewReading(long idJustSaved) {
        repo.findById(idJustSaved).ifPresent(r -> {
            try {
                // also avoid nulls in the push payload
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", r.getId());
                payload.put("temperatureC", r.getTemperatureC() != null ? r.getTemperatureC() : BigDecimal.ZERO);
                payload.put("humidityPct",  r.getHumidityPct()  != null ? r.getHumidityPct()  : BigDecimal.ZERO);
                payload.put("waterLevelPct",r.getWaterLevelPct()!= null ? r.getWaterLevelPct(): BigDecimal.ZERO);
                payload.put("createdAt",    r.getCreatedAt()    != null ? r.getCreatedAt().toString() : "");

                stream.push(mapper.writeValueAsString(payload));
            } catch (Exception ignored) {}
        });
    }
}

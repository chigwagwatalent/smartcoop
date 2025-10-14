package com.chicken.system.restcontroller;

import com.chicken.system.dto.CreateIotDataRequest;
import com.chicken.system.dto.IotDataResponse;
import com.chicken.system.entity.IotData;
import com.chicken.system.services.IotDataService;
import com.chicken.system.services.IotDataViewService;
import com.chicken.system.services.IotStreamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1/api/iot")
public class IotDataRestController {

    private final IotDataService service;
    private final IotDataViewService viewService;
    private final IotStreamService stream;
    private final ObjectMapper mapper;

    public IotDataRestController(IotDataViewService viewService, IotStreamService stream, ObjectMapper mapper, IotDataService service) {
        this.viewService = viewService;
        this.stream = stream;
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping(value = "/readings", consumes = "application/json", produces = "application/json")
    public ResponseEntity<IotDataResponse> create(@Valid @RequestBody CreateIotDataRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }
    
    @GetMapping("/readings")
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<IotData> p = viewService.page(page, size);
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", p.getContent());
        payload.put("page", p.getNumber());
        payload.put("size", p.getSize());
        payload.put("totalPages", p.getTotalPages());
        payload.put("totalElements", p.getTotalElements());
        return ResponseEntity.ok(payload);
    }
    
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() throws Exception {
        String first = mapper.writeValueAsString(viewService.latestSummary());
        return stream.subscribe(first);
    }
}

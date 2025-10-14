//package com.chicken.system.restcontroller;
//
//import com.chicken.system.entity.StockData;
//import com.chicken.system.services.StockDataService;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/stock")
//public class StockDataRestController {
//
//    private final StockDataService service;
//
//    public StockDataRestController(StockDataService service) {
//        this.service = service;
//    }
//
//    /** Paged list */
//    @GetMapping
//    public ResponseEntity<Map<String, Object>> page(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Page<StockData> p = service.page(page, size);
//        return ResponseEntity.ok(Map.of(
//                "content", p.getContent(),
//                "page", p.getNumber(),
//                "size", p.getSize(),
//                "totalPages", p.getTotalPages(),
//                "totalElements", p.getTotalElements()
//        ));
//    }
//
//    /** Get one (for edit modal) */
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getOne(@PathVariable Long id) {
//        return service.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    /** Create */
//    @PostMapping
//    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
//        String coopId = (String) body.getOrDefault("coopId", "");
//        Integer chicksCount = parseInt(body.get("chicksCount"));
//        if (coopId == null || coopId.isBlank() || chicksCount == null || chicksCount < 0) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Invalid data"));
//        }
//        StockData s = service.create(coopId, chicksCount);
//        return ResponseEntity.ok(Map.of("id", s.getId()));
//    }
//
//    /** Update */
//    @PutMapping("/{id}")
//    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
//        String coopId = (String) body.getOrDefault("coopId", "");
//        Integer chicksCount = parseInt(body.get("chicksCount"));
//        if (coopId == null || coopId.isBlank() || chicksCount == null || chicksCount < 0) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Invalid data"));
//        }
//        return service.update(id, coopId, chicksCount)
//                .map(s -> ResponseEntity.ok(Map.of("ok", true)))
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    /** Delete */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        return service.delete(id)
//                ? ResponseEntity.ok(Map.of("ok", true))
//                : ResponseEntity.notFound().build();
//    }
//
//    private static Integer parseInt(Object v) {
//        if (v == null) return null;
//        if (v instanceof Number n) return n.intValue();
//        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return null; }
//    }
//}

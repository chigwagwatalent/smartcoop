package com.chicken.system.services;

import com.chicken.system.entity.StockData;
import com.chicken.system.repository.StockDataRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StockDataService {

    private final StockDataRepository repo;

    public StockDataService(StockDataRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<StockData> page(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<StockData> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public StockData create(@NotBlank String coopId, @NotNull @Min(0) Integer chicksCount) {
        StockData s = new StockData();
        s.setCoopId(coopId);
        s.setChicksCount(chicksCount);
        return repo.save(s);
    }

    @Transactional
    public Optional<StockData> update(Long id, @NotBlank String coopId, @NotNull @Min(0) Integer chicksCount) {
        return repo.findById(id).map(existing -> {
            existing.setCoopId(coopId);
            existing.setChicksCount(chicksCount);
            return repo.save(existing);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}

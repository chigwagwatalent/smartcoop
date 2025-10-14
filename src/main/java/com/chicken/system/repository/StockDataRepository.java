package com.chicken.system.repository;

import com.chicken.system.entity.StockData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockDataRepository extends JpaRepository<StockData, Long> {
    Optional<StockData> findTop1ByOrderByCreatedAtDesc();
    List<StockData> findTop30ByOrderByCreatedAtDesc();
}

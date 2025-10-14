package com.chicken.system.repository;

import com.chicken.system.entity.IotData;
import org.springframework.data.domain.Page;       // ✅ Spring Data Page
import org.springframework.data.domain.Pageable; // ✅ Spring Data Pageable
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IotDataRepository extends JpaRepository<IotData, Long> {

    Optional<IotData> findTop1ByOrderByCreatedAtDesc();

    List<IotData> findAllByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime from, LocalDateTime to);

    List<IotData> findTop50ByOrderByCreatedAtDesc();

    Page<IotData> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

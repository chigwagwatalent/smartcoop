package com.chicken.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "stock_data",
    indexes = {
        @Index(name = "idx_stock_coop_id", columnList = "coop_id"),
        @Index(name = "idx_stock_created_at", columnList = "created_at")
    }
)
public class StockData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifier for the coop (e.g., "COOP-A1"). */
    @NotBlank
    @Column(name = "coop_id", length = 64, nullable = false)
    private String coopId;

    /** Number of chicks counted in this entry. */
    @NotNull
    @Min(0)
    @Column(name = "chicks_count", nullable = false)
    private Integer chicksCount;

    /** Auto-set on insert. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public String getCoopId() {
        return coopId;
    }

    public void setCoopId(String coopId) {
        this.coopId = coopId;
    }

    public Integer getChicksCount() {
        return chicksCount;
    }

    public void setChicksCount(Integer chicksCount) {
        this.chicksCount = chicksCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

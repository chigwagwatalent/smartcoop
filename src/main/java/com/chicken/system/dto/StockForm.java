package com.chicken.system.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockForm {

    private Long id;

    @NotBlank(message = "Coop ID is required")
    private String coopId;

    @NotNull(message = "Chicks count is required")
    @Min(value = 0, message = "Chicks count must be 0 or more")
    private Integer chicksCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoopId() { return coopId; }
    public void setCoopId(String coopId) { this.coopId = coopId; }

    public Integer getChicksCount() { return chicksCount; }
    public void setChicksCount(Integer chicksCount) { this.chicksCount = chicksCount; }
}

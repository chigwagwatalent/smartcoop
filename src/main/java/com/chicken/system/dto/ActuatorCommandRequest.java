package com.chicken.system.dto;

import jakarta.validation.constraints.NotNull;

public class ActuatorCommandRequest {
    @NotNull
    private Boolean on;

    public Boolean getOn() { return on; }
    public void setOn(Boolean on) { this.on = on; }
}

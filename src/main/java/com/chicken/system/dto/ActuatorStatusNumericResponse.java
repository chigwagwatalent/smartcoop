package com.chicken.system.dto;

// Numeric response: 1 = ON, 0 = OFF
public record ActuatorStatusNumericResponse(int pumpOn, int fanOn) {}

package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

import com.ltde.rutherford_d1.model.HealthStatus;

public record ParameterDTO(
    Long id,
    Double value,
    LocalDate datePerformed,
    HealthStatus status  // Health status based on reference range analysis
) {} 
package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

import com.ltde.rutherford_d1.model.HealthStatus;

/**
 * DTO representing an alert for a specific parameter that is outside normal range
 * Used for detailed alert information and trend analysis
 */
public record ParameterAlertDTO(
    Long parameterId,
    String testName,              // Name of the test this parameter belongs to
    String parameterName,         // Name of the parameter (e.g., "Hemoglobin")
    String unit,                  // Unit of measurement
    Double value,                 // Current measured value
    Double referenceMin,          // Reference range minimum
    Double referenceMax,          // Reference range maximum
    HealthStatus status,          // Current health status (LOW, HIGH, CRITICAL)
    LocalDate datePerformed,      // When this measurement was taken
    String alertMessage           // Human-readable alert description
) {} 
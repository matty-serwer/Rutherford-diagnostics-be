package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for detailed patient information including health summary
 * Used for patient detail views with comprehensive health data
 */
public record PatientDetailDTO(
    Long id,
    String name,
    String species,
    String breed,
    LocalDate dateOfBirth,
    String ownerName,
    String ownerContact,
    HealthSummaryDTO healthSummary,        // Overall health summary and metrics
    List<TestSummaryDTO> diagnosticHistory // Historical tests performed
) {} 
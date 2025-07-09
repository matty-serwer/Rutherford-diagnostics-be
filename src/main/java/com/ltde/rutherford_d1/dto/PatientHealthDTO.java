package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO that combines patient basic information with health analysis data
 * Used for patient health dashboard and overview screens
 */
public record PatientHealthDTO(
    Long id,
    String name,
    String species,
    String breed,
    LocalDate dateOfBirth,
    String ownerName,
    String ownerContact,
    HealthSummaryDTO healthSummary,           // Overall health summary
    List<ParameterDTO> abnormalParameters     // List of all abnormal parameters for quick review
) {} 
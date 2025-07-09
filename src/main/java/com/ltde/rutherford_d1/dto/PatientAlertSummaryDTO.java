package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

/**
 * DTO representing a summary of alerts for a patient
 * Used in the global alerts dashboard to show which patients need attention
 */
public record PatientAlertSummaryDTO(
    Long patientId,
    String patientName,
    String species,
    String breed,
    String ownerName,
    String ownerContact,
    int criticalCount,            // Number of critical parameters
    int abnormalCount,            // Total number of abnormal parameters
    int healthScore,              // Overall health score (0-100)
    LocalDate lastTestDate,       // Date of most recent test
    String mostCriticalAlert      // Description of the most severe current alert
) {} 
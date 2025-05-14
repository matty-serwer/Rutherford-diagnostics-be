package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;
import java.util.List;

public record PatientDetailDTO(
    Long id,
    String name,
    String species,
    String breed,
    LocalDate dateOfBirth,
    String ownerName,
    String ownerContact,
    List<TestSummaryDTO> diagnosticHistory
) {} 
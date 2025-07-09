package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;
import java.util.List;

public record TestDetailDTO(
    Long id,
    String name,
    LocalDate datePerformed,
    PatientDTO patient,
    String parameterName,
    String unit,
    Double referenceMin,
    Double referenceMax,
    List<ParameterDTO> parameters
) {} 
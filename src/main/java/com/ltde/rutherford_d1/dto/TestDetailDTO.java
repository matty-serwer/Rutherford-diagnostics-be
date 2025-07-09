package com.ltde.rutherford_d1.dto;

import java.util.List;

public record TestDetailDTO(
    Long id,
    String name,
    PatientDTO patient,
    String parameterName,
    String unit,
    Double referenceMin,
    Double referenceMax,
    List<ParameterDTO> parameters
) {} 
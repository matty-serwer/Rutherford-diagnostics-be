package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;
import java.util.List;

public record TestDetailDTO(
    Long id,
    String name,
    LocalDate datePerformed,
    PatientDTO patient,
    List<ParameterDTO> parameters
) {} 
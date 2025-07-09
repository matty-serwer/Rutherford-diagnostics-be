package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

public record ParameterDTO(
    Long id,
    Double value,
    LocalDate datePerformed
) {} 
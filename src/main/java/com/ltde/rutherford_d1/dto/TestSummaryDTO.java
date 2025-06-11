package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

public record TestSummaryDTO(
    Long id,
    String name,
    LocalDate datePerformed
) {} 
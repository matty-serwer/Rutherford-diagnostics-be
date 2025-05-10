package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

public record ResultHistoryDTO(
    LocalDate resultDate,
    Double value
) {} 
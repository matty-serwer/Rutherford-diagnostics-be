package com.ltde.rutherford_d1.dto;

import java.util.List;

public record ParameterDTO(
    Long id,
    String name,
    String unit,
    Double referenceMin,
    Double referenceMax,
    List<ResultHistoryDTO> history
) {} 
package com.ltde.rutherford_d1.dto;

import java.time.LocalDate;

public record PatientDTO(
    Long id,
    String name,
    String species,
    String breed,
    LocalDate dateOfBirth,
    String ownerName,
    String ownerContact
) {} 
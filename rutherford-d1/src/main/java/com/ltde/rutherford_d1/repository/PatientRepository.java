package com.ltde.rutherford_d1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ltde.rutherford_d1.model.Patient;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByNameAndOwnerNameAndDateOfBirth(String name, String ownerName, LocalDate dateOfBirth);
} 
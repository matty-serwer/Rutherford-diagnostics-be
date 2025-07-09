package com.ltde.rutherford_d1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ltde.rutherford_d1.dto.PatientDTO;
import com.ltde.rutherford_d1.dto.PatientDetailDTO;
import com.ltde.rutherford_d1.dto.TestSummaryDTO;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.repository.PatientRepository;

@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
            .map(this::toPatientDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDetailDTO> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
            .map(this::toPatientDetailDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private PatientDTO toPatientDTO(Patient patient) {
        return new PatientDTO(
            patient.getId(),
            patient.getName(),
            patient.getSpecies(),
            patient.getBreed(),
            patient.getDateOfBirth(),
            patient.getOwnerName(),
            patient.getOwnerContact()
        );
    }

    private PatientDetailDTO toPatientDetailDTO(Patient patient) {
        List<TestSummaryDTO> tests = patient.getTests().stream()
            .map(test -> new TestSummaryDTO(
                test.getId(),
                test.getName()
            ))
            .collect(Collectors.toList());

        return new PatientDetailDTO(
            patient.getId(),
            patient.getName(),
            patient.getSpecies(),
            patient.getBreed(),
            patient.getDateOfBirth(),
            patient.getOwnerName(),
            patient.getOwnerContact(),
            tests
        );
    }
}
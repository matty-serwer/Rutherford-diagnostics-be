package com.ltde.rutherford_d1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ltde.rutherford_d1.dto.HealthSummaryDTO;
import com.ltde.rutherford_d1.dto.PatientDTO;
import com.ltde.rutherford_d1.dto.PatientDetailDTO;
import com.ltde.rutherford_d1.dto.TestSummaryDTO;
import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.PatientRepository;
import com.ltde.rutherford_d1.service.HealthAnalysisService;

@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientRepository patientRepository;
    private final HealthAnalysisService healthAnalysisService;

    public PatientController(PatientRepository patientRepository, HealthAnalysisService healthAnalysisService) {
        this.patientRepository = patientRepository;
        this.healthAnalysisService = healthAnalysisService;
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
        // Create health summary
        HealthSummaryDTO healthSummary = createHealthSummary(patient);
        
        // Create diagnostic history
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
            healthSummary,
            tests
        );
    }

    /**
     * Create HealthSummaryDTO for a patient
     */
    private HealthSummaryDTO createHealthSummary(Patient patient) {
        List<Parameter> allParameters = patient.getTests().stream()
            .flatMap(test -> test.getParameters().stream())
            .collect(Collectors.toList());
        
        // Ensure all parameters have status calculated
        allParameters.forEach(parameter -> {
            if (parameter.getStatus() == null) {
                Test test = parameter.getTest();
                HealthStatus status = healthAnalysisService.calculateParameterStatus(
                    parameter.getValue(), test.getReferenceMin(), test.getReferenceMax());
                parameter.setStatus(status);
            }
        });
        
        int totalParameters = allParameters.size();
        int normalCount = (int) allParameters.stream().filter(p -> p.getStatus() == HealthStatus.NORMAL).count();
        int lowCount = (int) allParameters.stream().filter(p -> p.getStatus() == HealthStatus.LOW).count();
        int highCount = (int) allParameters.stream().filter(p -> p.getStatus() == HealthStatus.HIGH).count();
        int criticalCount = (int) allParameters.stream().filter(p -> p.getStatus() == HealthStatus.CRITICAL).count();
        int abnormalCount = totalParameters - normalCount;
        
        int healthScore = healthAnalysisService.getHealthScore(patient);
        
        return new HealthSummaryDTO(
            healthScore,
            totalParameters,
            normalCount,
            lowCount,
            highCount,
            criticalCount,
            abnormalCount
        );
    }
}
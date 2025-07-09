package com.ltde.rutherford_d1.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ltde.rutherford_d1.dto.HealthSummaryDTO;
import com.ltde.rutherford_d1.dto.ParameterAlertDTO;
import com.ltde.rutherford_d1.dto.ParameterDTO;
import com.ltde.rutherford_d1.dto.PatientAlertSummaryDTO;
import com.ltde.rutherford_d1.dto.PatientHealthDTO;
import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.PatientRepository;
import com.ltde.rutherford_d1.service.HealthAnalysisService;

/**
 * Controller for health-related endpoints providing health summaries and alerts
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    
    private final PatientRepository patientRepository;
    private final HealthAnalysisService healthAnalysisService;

    public HealthController(PatientRepository patientRepository, HealthAnalysisService healthAnalysisService) {
        this.patientRepository = patientRepository;
        this.healthAnalysisService = healthAnalysisService;
    }
    
    /**
     * Get comprehensive health summary for a specific patient
     */
    @GetMapping("/patient/{id}/summary")
    public ResponseEntity<PatientHealthDTO> getPatientHealthSummary(@PathVariable Long id) {
        return patientRepository.findById(id)
            .map(this::toPatientHealthDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get detailed alerts for a specific patient's abnormal parameters
     */
    @GetMapping("/patient/{id}/alerts")
    public ResponseEntity<List<ParameterAlertDTO>> getPatientAlerts(@PathVariable Long id) {
        return patientRepository.findById(id)
            .map(patient -> {
                List<Parameter> abnormalParameters = healthAnalysisService.getAbnormalParameters(patient);
                return abnormalParameters.stream()
                    .map(this::toParameterAlertDTO)
                    .sorted(Comparator.comparing((ParameterAlertDTO alert) -> alert.status() == HealthStatus.CRITICAL ? 0 : 1)
                            .thenComparing(ParameterAlertDTO::datePerformed).reversed())
                    .collect(Collectors.toList());
            })
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get summary of all patients with active health alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<PatientAlertSummaryDTO>> getAllActiveAlerts() {
        List<PatientAlertSummaryDTO> alerts = patientRepository.findAll().stream()
            .map(this::toPatientAlertSummaryDTO)
            .filter(alert -> alert.abnormalCount() > 0) // Only include patients with alerts
            .sorted(Comparator.comparing((PatientAlertSummaryDTO alert) -> alert.criticalCount()).reversed()
                    .thenComparing(PatientAlertSummaryDTO::abnormalCount).reversed())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(alerts);
    }

    /**
     * Convert Patient entity to PatientHealthDTO with full health analysis
     */
    private PatientHealthDTO toPatientHealthDTO(Patient patient) {
        HealthSummaryDTO healthSummary = createHealthSummary(patient);
        List<Parameter> abnormalParameters = healthAnalysisService.getAbnormalParameters(patient);
        
        List<ParameterDTO> abnormalParameterDTOs = abnormalParameters.stream()
            .map(this::toParameterDTO)
            .sorted(Comparator.comparing((ParameterDTO param) -> param.status() == HealthStatus.CRITICAL ? 0 : 1)
                    .thenComparing(ParameterDTO::datePerformed).reversed())
            .collect(Collectors.toList());

        return new PatientHealthDTO(
            patient.getId(),
            patient.getName(),
            patient.getSpecies(),
            patient.getBreed(),
            patient.getDateOfBirth(),
            patient.getOwnerName(),
            patient.getOwnerContact(),
            healthSummary,
            abnormalParameterDTOs
        );
    }

    /**
     * Convert Parameter to ParameterAlertDTO with alert context
     */
    private ParameterAlertDTO toParameterAlertDTO(Parameter parameter) {
        Test test = parameter.getTest();
        HealthStatus status = parameter.getStatus();
        
        // Generate human-readable alert message
        String alertMessage = generateAlertMessage(parameter, test, status);
        
        return new ParameterAlertDTO(
            parameter.getId(),
            test.getName(),
            test.getParameterName(),
            test.getUnit(),
            parameter.getValue(),
            test.getReferenceMin(),
            test.getReferenceMax(),
            status,
            parameter.getDatePerformed(),
            alertMessage
        );
    }

    /**
     * Convert Patient to PatientAlertSummaryDTO for global alerts dashboard
     */
    private PatientAlertSummaryDTO toPatientAlertSummaryDTO(Patient patient) {
        HealthSummaryDTO healthSummary = createHealthSummary(patient);
        
        // Find most recent test date
        LocalDate lastTestDate = patient.getTests().stream()
            .flatMap(test -> test.getParameters().stream())
            .map(Parameter::getDatePerformed)
            .max(Comparator.naturalOrder())
            .orElse(null);
        
        // Generate most critical alert message
        String mostCriticalAlert = generateMostCriticalAlert(patient);
        
        return new PatientAlertSummaryDTO(
            patient.getId(),
            patient.getName(),
            patient.getSpecies(),
            patient.getBreed(),
            patient.getOwnerName(),
            patient.getOwnerContact(),
            healthSummary.criticalCount(),
            healthSummary.abnormalCount(),
            healthSummary.healthScore(),
            lastTestDate,
            mostCriticalAlert
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

    /**
     * Convert Parameter to ParameterDTO with health status
     */
    private ParameterDTO toParameterDTO(Parameter parameter) {
        HealthStatus status = parameter.getStatus();
        if (status == null) {
            Test test = parameter.getTest();
            status = healthAnalysisService.calculateParameterStatus(
                parameter.getValue(), test.getReferenceMin(), test.getReferenceMax());
        }
        
        return new ParameterDTO(
            parameter.getId(),
            parameter.getValue(),
            parameter.getDatePerformed(),
            status
        );
    }

    /**
     * Generate human-readable alert message for a parameter
     */
    private String generateAlertMessage(Parameter parameter, Test test, HealthStatus status) {
        String paramName = test.getParameterName();
        Double value = parameter.getValue();
        String unit = test.getUnit();
        Double min = test.getReferenceMin();
        Double max = test.getReferenceMax();
        
        return switch (status) {
            case CRITICAL -> {
                if (value < min) {
                    yield String.format("%s critically low: %.2f %s (normal: %.1f-%.1f %s)", 
                        paramName, value, unit, min, max, unit);
                } else {
                    yield String.format("%s critically high: %.2f %s (normal: %.1f-%.1f %s)", 
                        paramName, value, unit, min, max, unit);
                }
            }
            case LOW -> String.format("%s below normal: %.2f %s (normal: %.1f-%.1f %s)", 
                paramName, value, unit, min, max, unit);
            case HIGH -> String.format("%s above normal: %.2f %s (normal: %.1f-%.1f %s)", 
                paramName, value, unit, min, max, unit);
            default -> String.format("%s: %.2f %s (normal)", paramName, value, unit);
        };
    }

    /**
     * Generate the most critical alert message for a patient
     */
    private String generateMostCriticalAlert(Patient patient) {
        List<Parameter> abnormalParameters = healthAnalysisService.getAbnormalParameters(patient);
        
        if (abnormalParameters.isEmpty()) {
            return "No active alerts";
        }
        
        // Find the most critical parameter (prioritize CRITICAL status, then most recent)
        Parameter mostCritical = abnormalParameters.stream()
            .sorted(Comparator.comparing((Parameter p) -> p.getStatus() == HealthStatus.CRITICAL ? 0 : 1)
                    .thenComparing(Parameter::getDatePerformed).reversed())
            .findFirst()
            .orElse(null);
        
        if (mostCritical != null) {
            return generateAlertMessage(mostCritical, mostCritical.getTest(), mostCritical.getStatus());
        }
        
        return "Multiple alerts detected";
    }
} 
package com.ltde.rutherford_d1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ltde.rutherford_d1.dto.ParameterDTO;
import com.ltde.rutherford_d1.dto.PatientDTO;
import com.ltde.rutherford_d1.dto.TestDetailDTO;
import com.ltde.rutherford_d1.dto.TestSummaryDTO;
import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.TestRepository;
import com.ltde.rutherford_d1.service.HealthAnalysisService;

@RestController
@RequestMapping("/test")
public class TestController {
    private final TestRepository testRepository;
    private final HealthAnalysisService healthAnalysisService;

    public TestController(TestRepository testRepository, HealthAnalysisService healthAnalysisService) {
        this.testRepository = testRepository;
        this.healthAnalysisService = healthAnalysisService;
    }

    @GetMapping
    public List<TestSummaryDTO> getAllTests() {
        return testRepository.findAll().stream()
            .map(this::toTestSummaryDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDetailDTO> getTestById(@PathVariable Long id) {
        return testRepository.findById(id)
            .map(this::toTestDetailDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private TestSummaryDTO toTestSummaryDTO(Test test) {
        return new TestSummaryDTO(
            test.getId(),
            test.getName()
        );
    }

    private TestDetailDTO toTestDetailDTO(Test test) {
        return new TestDetailDTO(
            test.getId(),
            test.getName(),
            toPatientDTO(test.getPatient()),
            test.getParameterName(),
            test.getUnit(),
            test.getReferenceMin(),
            test.getReferenceMax(),
            test.getParameters().stream()
                .map(this::toParameterDTO)
                .collect(Collectors.toList())
        );
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

    private ParameterDTO toParameterDTO(Parameter parameter) {
        // Calculate health status if not already set
        HealthStatus status = parameter.getStatus();
        if (status == null) {
            Test test = parameter.getTest();
            status = healthAnalysisService.calculateParameterStatus(
                parameter.getValue(), 
                test.getReferenceMin(), 
                test.getReferenceMax()
            );
        }
        
        return new ParameterDTO(
            parameter.getId(),
            parameter.getValue(),
            parameter.getDatePerformed(),
            status  // Include the health status in the DTO
        );
    }
} 
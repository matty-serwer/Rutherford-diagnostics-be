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
import com.ltde.rutherford_d1.dto.ResultHistoryDTO;
import com.ltde.rutherford_d1.dto.TestDetailDTO;
import com.ltde.rutherford_d1.dto.TestSummaryDTO;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.TestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tests")
@Tag(name = "Diagnostic Tests", description = "Diagnostic Test Management API")
public class TestController {
    private final TestRepository testRepository;

    public TestController(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Operation(summary = "Get all diagnostic tests", description = "Returns a list of all available diagnostic tests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved test list", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TestSummaryDTO.class)))
    })
    @GetMapping
    public List<TestSummaryDTO> getAllTests() {
        return testRepository.findAll().stream()
            .map(this::toTestSummaryDTO)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Get test by ID", description = "Returns detailed test information with parameters and result history")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved test",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TestDetailDTO.class))),
        @ApiResponse(responseCode = "404", description = "Test not found", 
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TestDetailDTO> getTestById(
            @io.swagger.v3.oas.annotations.Parameter(description = "Test ID", required = true)
            @PathVariable Long id) {
        return testRepository.findById(id)
            .map(this::toTestDetailDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private TestSummaryDTO toTestSummaryDTO(Test test) {
        return new TestSummaryDTO(
            test.getId(),
            test.getName(),
            test.getDatePerformed()
        );
    }

    private TestDetailDTO toTestDetailDTO(Test test) {
        return new TestDetailDTO(
            test.getId(),
            test.getName(),
            test.getDatePerformed(),
            toPatientDTO(test.getPatient()),
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
        List<ResultHistoryDTO> history = parameter.getHistory().stream()
            .map(result -> new ResultHistoryDTO(
                result.getResultDate(),
                result.getValue()
            ))
            .collect(Collectors.toList());

        return new ParameterDTO(
            parameter.getId(),
            parameter.getName(),
            parameter.getUnit(),
            parameter.getReferenceMin(),
            parameter.getReferenceMax(),
            history
        );
    }
} 
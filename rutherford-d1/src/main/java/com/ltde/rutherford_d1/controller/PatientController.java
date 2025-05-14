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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "Patient Management API")
public class PatientController {
    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Operation(summary = "Get all patients", description = "Returns a list of all patients with basic information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved patient list", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = PatientDTO.class)))
    })
    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
            .map(this::toPatientDTO)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Get patient by ID", description = "Returns detailed patient information with diagnostic history")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved patient",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientDetailDTO.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found", 
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientDetailDTO> getPatientById(
            @Parameter(description = "Patient ID", required = true)
            @PathVariable Long id) {
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
                test.getName(),
                test.getDatePerformed()
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
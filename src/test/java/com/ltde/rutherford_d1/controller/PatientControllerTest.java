package com.ltde.rutherford_d1.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.repository.PatientRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    private Patient testPatient;

    /**
     * This method is called before each test method due to @BeforeEach annotation.
     * It sets up the test data required for each test case.
     */
    @BeforeEach
    void setUp() {
        // Delete all existing data
        patientRepository.deleteAll();
        
        testPatient = new Patient();
        testPatient.setName("TestDog");
        testPatient.setSpecies("Dog");
        testPatient.setBreed("Labrador");
        testPatient.setDateOfBirth(LocalDate.of(2020, 1, 1));
        testPatient.setOwnerName("Test Owner");
        testPatient.setOwnerContact("123-456-7890");
        testPatient.setTests(new ArrayList<>());
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    void getAllPatients_ShouldReturnPatientsList() throws Exception {
        mockMvc.perform(get("/patient"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].name", is("TestDog")))
            .andExpect(jsonPath("$[0].species", is("Dog")))
            .andExpect(jsonPath("$[0].breed", is("Labrador")))
            .andExpect(jsonPath("$[0].ownerName", is("Test Owner")));
    }

    @Test
    void getPatientById_WithValidId_ShouldReturnPatient() throws Exception {
        mockMvc.perform(get("/patient/{id}", testPatient.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("TestDog")))
            .andExpect(jsonPath("$.species", is("Dog")))
            .andExpect(jsonPath("$.breed", is("Labrador")))
            .andExpect(jsonPath("$.ownerName", is("Test Owner")))
            .andExpect(jsonPath("$.diagnosticHistory", hasSize(0)));
    }

    @Test
    void getPatientById_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/patient/{id}", 999L))
            .andExpect(status().isNotFound());
    }
} 
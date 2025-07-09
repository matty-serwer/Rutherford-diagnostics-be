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

import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.repository.ParameterRepository;
import com.ltde.rutherford_d1.repository.PatientRepository;
import com.ltde.rutherford_d1.repository.TestRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    private com.ltde.rutherford_d1.model.Test testDiagnostic;
    private Patient testPatient;
    private Parameter testParameter;

    /**
     * This method is called before each test method due to @BeforeEach annotation.
     * It sets up the test data required for each test case.
     */
    @BeforeEach
    void setUp() {
        // Delete all existing data
        parameterRepository.deleteAll();
        testRepository.deleteAll();
        patientRepository.deleteAll();

        // Create test patient
        testPatient = new Patient();
        testPatient.setName("TestDog");
        testPatient.setSpecies("Dog");
        testPatient.setBreed("Labrador");
        testPatient.setDateOfBirth(LocalDate.of(2020, 1, 1));
        testPatient.setOwnerName("Test Owner");
        testPatient.setOwnerContact("123-456-7890");
        testPatient.setTests(new ArrayList<>());
        testPatient = patientRepository.save(testPatient);

        // Create test diagnostic with shared parameter properties (no datePerformed)
        testDiagnostic = new com.ltde.rutherford_d1.model.Test();
        testDiagnostic.setName("Blood Test");
        testDiagnostic.setParameterName("Hemoglobin");
        testDiagnostic.setUnit("g/dL");
        testDiagnostic.setReferenceMin(12.0);
        testDiagnostic.setReferenceMax(18.0);
        testDiagnostic.setPatient(testPatient);
        testDiagnostic.setParameters(new ArrayList<>());
        testDiagnostic = testRepository.save(testDiagnostic);

        // Add test to patient's tests list
        testPatient.getTests().add(testDiagnostic);
        testPatient = patientRepository.save(testPatient);

        // Create test parameter with value and datePerformed
        testParameter = new Parameter();
        testParameter.setValue(15.0);
        testParameter.setDatePerformed(LocalDate.now());
        testParameter.setTest(testDiagnostic);
        testParameter = parameterRepository.save(testParameter);

        // Add parameter to test's parameters list
        testDiagnostic.getParameters().add(testParameter);
        testDiagnostic = testRepository.save(testDiagnostic);
    }

    @Test
    void getAllTests_ShouldReturnTestsList() throws Exception {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].name", is("Blood Test")));
    }

    @Test
    void getTestById_WithValidId_ShouldReturnTest() throws Exception {
        mockMvc.perform(get("/test/{id}", testDiagnostic.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Blood Test")))
            .andExpect(jsonPath("$.patient.name", is("TestDog")))
            .andExpect(jsonPath("$.parameterName", is("Hemoglobin")))
            .andExpect(jsonPath("$.unit", is("g/dL")))
            .andExpect(jsonPath("$.referenceMin", is(12.0)))
            .andExpect(jsonPath("$.referenceMax", is(18.0)))
            .andExpect(jsonPath("$.parameters", hasSize(1)))
            .andExpect(jsonPath("$.parameters[0].value", is(15.0)))
            .andExpect(jsonPath("$.parameters[0].datePerformed", is(LocalDate.now().toString())));
    }

    @Test
    void getTestById_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/test/{id}", 999L))
            .andExpect(status().isNotFound());
    }
} 
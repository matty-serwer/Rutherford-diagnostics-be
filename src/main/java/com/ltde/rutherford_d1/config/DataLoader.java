package com.ltde.rutherford_d1.config;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.ParameterRepository;
import com.ltde.rutherford_d1.repository.PatientRepository;
import com.ltde.rutherford_d1.repository.TestRepository;
import com.ltde.rutherford_d1.service.HealthAnalysisService;

@Component
public class DataLoader implements CommandLineRunner {
    private final PatientRepository patientRepository;
    private final TestRepository testRepository;
    private final ParameterRepository parameterRepository;
    private final HealthAnalysisService healthAnalysisService;

    public DataLoader(PatientRepository patientRepository,
                      TestRepository testRepository,
                      ParameterRepository parameterRepository,
                      HealthAnalysisService healthAnalysisService) {
        this.patientRepository = patientRepository;
        this.testRepository = testRepository;
        this.parameterRepository = parameterRepository;
        this.healthAnalysisService = healthAnalysisService;
    }

    @Override
    public void run(String... args) {
        // Option to clear database and start fresh
        boolean clearDatabase = true; // Set to true if you need to reset the database
        
        if (clearDatabase) {
            clearAllData();
        }
        
        if (patientRepository.count() > 0) {
            System.out.println("Database already contains data - skipping data load");
            return;
        }

        createPatientsWithEnhancedTests();
        System.out.println("Data loading completed - Patient count: " + patientRepository.count());
    }

    private void clearAllData() {
        System.out.println("Clearing all data from database...");
        parameterRepository.deleteAll();
        testRepository.deleteAll();
        patientRepository.deleteAll();
        System.out.println("Database cleared successfully");
    }

    private void createPatientsWithEnhancedTests() {
        // Create Walter - Healthy patient with mostly normal values
        Patient walter = createPatient("Walter", "Dog", "Labrador", 
            LocalDate.of(2018, 5, 20), "Jane Doe", "555-1234");
        
        createTestForPatient(walter, "Complete Blood Count", 
            "Hemoglobin", "g/dL", 12.0, 18.0,
            new ParameterData(14.5, LocalDate.of(2024, 3, 14)),  // Normal
            new ParameterData(13.8, LocalDate.of(2024, 2, 14)),  // Normal
            new ParameterData(15.2, LocalDate.of(2024, 1, 12)),  // Normal
            new ParameterData(15.1, LocalDate.of(2023, 12, 24)), // Normal
            new ParameterData(13.1, LocalDate.of(2023, 11, 21)), // Normal
            new ParameterData(14.8, LocalDate.of(2023, 10, 18)), // Normal
            new ParameterData(16.0, LocalDate.of(2023, 9, 15)),  // Normal
            new ParameterData(13.9, LocalDate.of(2023, 8, 12)),  // Normal
            new ParameterData(14.3, LocalDate.of(2023, 7, 10))); // Normal
            
        createTestForPatient(walter, "Chemistry Panel", 
            "Glucose", "mg/dL", 70.0, 140.0,
            new ParameterData(95.0, LocalDate.of(2024, 2, 14)),  // Normal
            new ParameterData(88.0, LocalDate.of(2024, 1, 14)),  // Normal
            new ParameterData(102.0, LocalDate.of(2023, 12, 14)), // Normal
            new ParameterData(91.5, LocalDate.of(2023, 11, 14)), // Normal
            new ParameterData(108.0, LocalDate.of(2023, 10, 14)), // Normal
            new ParameterData(85.0, LocalDate.of(2023, 9, 14)),  // Normal
            new ParameterData(125.0, LocalDate.of(2023, 8, 14)), // Normal
            new ParameterData(98.5, LocalDate.of(2023, 7, 14))); // Normal

        // Create McGrupp - Patient with some mild abnormalities
        Patient mcgrupp = createPatient("McGrupp", "Dog", "German Shepherd", 
            LocalDate.of(2019, 8, 15), "John Smith", "555-5678");
            
        createTestForPatient(mcgrupp, "Complete Blood Count", 
            "Hemoglobin", "g/dL", 12.0, 18.0,
            new ParameterData(19.2, LocalDate.of(2024, 2, 20)),  // HIGH
            new ParameterData(18.8, LocalDate.of(2024, 1, 20)),  // HIGH
            new ParameterData(16.5, LocalDate.of(2023, 12, 20)), // Normal
            new ParameterData(15.9, LocalDate.of(2023, 11, 20)), // Normal
            new ParameterData(17.8, LocalDate.of(2023, 10, 20)), // Normal
            new ParameterData(18.2, LocalDate.of(2023, 9, 20)),  // HIGH
            new ParameterData(16.1, LocalDate.of(2023, 8, 20)),  // Normal
            new ParameterData(17.5, LocalDate.of(2023, 7, 20)),  // Normal
            new ParameterData(18.5, LocalDate.of(2023, 6, 20))); // HIGH
            
        createTestForPatient(mcgrupp, "Thyroid Panel", 
            "T4", "ug/dL", 1.0, 4.0,
            new ParameterData(0.7, LocalDate.of(2024, 1, 20)),  // LOW
            new ParameterData(0.8, LocalDate.of(2023, 12, 20)), // LOW
            new ParameterData(1.2, LocalDate.of(2023, 11, 20)), // Normal
            new ParameterData(1.8, LocalDate.of(2023, 10, 20)), // Normal
            new ParameterData(2.1, LocalDate.of(2023, 9, 20)),  // Normal
            new ParameterData(1.5, LocalDate.of(2023, 8, 20)),  // Normal
            new ParameterData(0.9, LocalDate.of(2023, 7, 20)),  // LOW
            new ParameterData(1.3, LocalDate.of(2023, 6, 20)),  // Normal
            new ParameterData(0.6, LocalDate.of(2023, 5, 20))); // LOW

        // Create Joan d'Bark - Patient with critical health issues (for demo)
        Patient joan = createPatient("Joan d'Bark", "Dog", "Golden Retriever", 
            LocalDate.of(2020, 3, 10), "Alice Johnson", "555-9012");
            
        createTestForPatient(joan, "Complete Blood Count", 
            "Hemoglobin", "g/dL", 12.0, 18.0,
            new ParameterData(8.2, LocalDate.of(2024, 2, 5)),   // CRITICAL (very low)
            new ParameterData(9.1, LocalDate.of(2024, 1, 5)),   // LOW
            new ParameterData(10.8, LocalDate.of(2023, 12, 5)), // LOW
            new ParameterData(8.9, LocalDate.of(2023, 11, 5)),  // LOW
            new ParameterData(7.5, LocalDate.of(2023, 10, 5)),  // CRITICAL (very low)
            new ParameterData(9.8, LocalDate.of(2023, 9, 5)),   // LOW
            new ParameterData(8.0, LocalDate.of(2023, 8, 5)),   // CRITICAL (very low)
            new ParameterData(10.2, LocalDate.of(2023, 7, 5)),  // LOW
            new ParameterData(11.5, LocalDate.of(2023, 6, 5))); // LOW
            
        createTestForPatient(joan, "Liver Function", 
            "ALT", "U/L", 10.0, 80.0,
            new ParameterData(145.0, LocalDate.of(2024, 1, 5)), // HIGH
            new ParameterData(158.0, LocalDate.of(2023, 12, 5)), // HIGH
            new ParameterData(172.0, LocalDate.of(2023, 11, 5)), // CRITICAL (very high)
            new ParameterData(139.0, LocalDate.of(2023, 10, 5)), // HIGH
            new ParameterData(185.0, LocalDate.of(2023, 9, 5)),  // CRITICAL (very high)
            new ParameterData(162.0, LocalDate.of(2023, 8, 5)),  // HIGH
            new ParameterData(198.0, LocalDate.of(2023, 7, 5)),  // CRITICAL (very high)
            new ParameterData(155.0, LocalDate.of(2023, 6, 5)),  // HIGH
            new ParameterData(176.0, LocalDate.of(2023, 5, 5))); // CRITICAL (very high)
            
        createTestForPatient(joan, "Kidney Function", 
            "Creatinine", "mg/dL", 0.5, 1.8,
            new ParameterData(3.2, LocalDate.of(2024, 1, 5)),   // CRITICAL (very high)
            new ParameterData(2.9, LocalDate.of(2023, 12, 5)),  // HIGH
            new ParameterData(2.1, LocalDate.of(2023, 11, 5)),  // HIGH
            new ParameterData(3.5, LocalDate.of(2023, 10, 5)),  // CRITICAL (very high)
            new ParameterData(2.7, LocalDate.of(2023, 9, 5)),   // HIGH
            new ParameterData(3.8, LocalDate.of(2023, 8, 5)),   // CRITICAL (very high)
            new ParameterData(2.4, LocalDate.of(2023, 7, 5)),   // HIGH
            new ParameterData(3.1, LocalDate.of(2023, 6, 5)),   // CRITICAL (very high)
            new ParameterData(2.8, LocalDate.of(2023, 5, 5)));  // HIGH
    }

    // Helper class to hold parameter data with date
    private static class ParameterData {
        final Double value;
        final LocalDate date;
        
        ParameterData(Double value, LocalDate date) {
            this.value = value;
            this.date = date;
        }
    }

    private Patient createPatient(String name, String species, String breed, 
            LocalDate dob, String ownerName, String ownerContact) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setSpecies(species);
        patient.setBreed(breed);
        patient.setDateOfBirth(dob);
        patient.setOwnerName(ownerName);
        patient.setOwnerContact(ownerContact);
        patient.setTests(new ArrayList<>());
        return patientRepository.save(patient);
    }

    private void createTestForPatient(Patient patient, String testName, 
                                     String parameterName, String unit, Double referenceMin, Double referenceMax,
                                     ParameterData... parameterDataArray) {
        // Create Test with shared parameter properties
        Test test = new Test();
        test.setName(testName);
        test.setParameterName(parameterName);
        test.setUnit(unit);
        test.setReferenceMin(referenceMin);
        test.setReferenceMax(referenceMax);
        test.setPatient(patient);
        test.setParameters(new ArrayList<>());
        test = testRepository.save(test);

        // Create Parameters with individual values, dates, and calculated health status
        for (ParameterData paramData : parameterDataArray) {
            Parameter parameter = new Parameter();
            parameter.setValue(paramData.value);
            parameter.setDatePerformed(paramData.date);
            parameter.setTest(test);
            
            // Calculate and set health status using the service
            HealthStatus status = healthAnalysisService.calculateParameterStatus(
                paramData.value, referenceMin, referenceMax);
            parameter.setStatus(status);
            
            parameter = parameterRepository.save(parameter);

            // Add parameter to test
            test.getParameters().add(parameter);
        }
        
        testRepository.save(test);
        
        // Add test to patient
        patient.getTests().add(test);
        patientRepository.save(patient);
    }
} 
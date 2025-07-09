package com.ltde.rutherford_d1.config;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.ParameterRepository;
import com.ltde.rutherford_d1.repository.PatientRepository;
import com.ltde.rutherford_d1.repository.TestRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final PatientRepository patientRepository;
    private final TestRepository testRepository;
    private final ParameterRepository parameterRepository;

    public DataLoader(PatientRepository patientRepository,
                      TestRepository testRepository,
                      ParameterRepository parameterRepository) {
        this.patientRepository = patientRepository;
        this.testRepository = testRepository;
        this.parameterRepository = parameterRepository;
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

        createPatientsWithMultipleTests();
        System.out.println("Data loading completed - Patient count: " + patientRepository.count());
    }

    private void clearAllData() {
        System.out.println("Clearing all data from database...");
        parameterRepository.deleteAll();
        testRepository.deleteAll();
        patientRepository.deleteAll();
        System.out.println("Database cleared successfully");
    }

    private void createPatientsWithMultipleTests() {
        // Create Walter with multiple tests and measurements over time
        Patient walter = createPatient("Walter", "Dog", "Labrador", 
            LocalDate.of(2018, 5, 20), "Jane Doe", "555-1234");
        
        createTestForPatient(walter, "Complete Blood Count", 
            "Hemoglobin", "g/dL", 12.0, 18.0,
            new ParameterData(14.5, LocalDate.of(2024, 3, 14)),
            new ParameterData(13.8, LocalDate.of(2024, 2, 14)),
            new ParameterData(15.2, LocalDate.of(2024, 1, 12)),
            new ParameterData(15.2, LocalDate.of(2023, 9, 24)),
            new ParameterData(13.1, LocalDate.of(2022, 4, 21)));
            
        createTestForPatient(walter, "Chemistry Panel", 
            "Glucose", "mg/dL", 70.0, 140.0,
            new ParameterData(95.0, LocalDate.of(2024, 2, 14)),
            new ParameterData(88.0, LocalDate.of(2024, 1, 14)),
            new ParameterData(102.0, LocalDate.of(2023, 12, 14)),
            new ParameterData(91.5, LocalDate.of(2023, 11, 14)));
            
        createTestForPatient(walter, "Thyroid Panel", 
            "T4", "ug/dL", 1.0, 4.0,
            new ParameterData(2.8, LocalDate.of(2024, 1, 14)),
            new ParameterData(2.5, LocalDate.of(2023, 10, 14)),
            new ParameterData(3.1, LocalDate.of(2023, 7, 14)),
            new ParameterData(2.9, LocalDate.of(2023, 4, 14)));

        // Create McGrupp with multiple tests and measurements over time
        Patient mcgrupp = createPatient("McGrupp", "Dog", "German Shepherd", 
            LocalDate.of(2019, 8, 15), "John Smith", "555-5678");
            
        createTestForPatient(mcgrupp, "Complete Blood Count", 
            "Hemoglobin", "g/dL", 12.0, 18.0,
            new ParameterData(16.1, LocalDate.of(2024, 2, 20)),
            new ParameterData(15.8, LocalDate.of(2024, 1, 20)),
            new ParameterData(16.5, LocalDate.of(2023, 12, 20)),
            new ParameterData(15.9, LocalDate.of(2023, 11, 20)));
            
        createTestForPatient(mcgrupp, "Chemistry Panel", 
            "Glucose", "mg/dL", 70.0, 140.0,
            new ParameterData(88.0, LocalDate.of(2024, 1, 20)),
            new ParameterData(92.0, LocalDate.of(2023, 12, 20)),
            new ParameterData(85.5, LocalDate.of(2023, 11, 20)),
            new ParameterData(90.0, LocalDate.of(2023, 10, 20)),
            new ParameterData(87.2, LocalDate.of(2023, 9, 20)));

        // Create Joan d'Bark with multiple tests and measurements over time
        Patient joan = createPatient("Joan d'Bark", "Dog", "Golden Retriever", 
            LocalDate.of(2020, 3, 10), "Alice Johnson", "555-9012");
            
        createTestForPatient(joan, "Complete Blood Count", 
            "Hemoglobin", "g/dL", 12.0, 18.0,
            new ParameterData(13.2, LocalDate.of(2024, 1, 5)),
            new ParameterData(12.8, LocalDate.of(2023, 12, 5)),
            new ParameterData(13.5, LocalDate.of(2023, 11, 5)));
            
        createTestForPatient(joan, "Liver Function", 
            "ALT", "U/L", 10.0, 80.0,
            new ParameterData(35.0, LocalDate.of(2023, 12, 5)),
            new ParameterData(28.0, LocalDate.of(2023, 11, 5)),
            new ParameterData(42.0, LocalDate.of(2023, 10, 5)),
            new ParameterData(31.5, LocalDate.of(2023, 9, 5)),
            new ParameterData(38.2, LocalDate.of(2023, 8, 5)),
            new ParameterData(33.1, LocalDate.of(2023, 7, 5)));
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
        // Create Test with shared parameter properties (no datePerformed)
        Test test = new Test();
        test.setName(testName);
        test.setParameterName(parameterName);
        test.setUnit(unit);
        test.setReferenceMin(referenceMin);
        test.setReferenceMax(referenceMax);
        test.setPatient(patient);
        test.setParameters(new ArrayList<>());
        test = testRepository.save(test);

        // Create Parameters with individual values and dates
        for (ParameterData paramData : parameterDataArray) {
            Parameter parameter = new Parameter();
            parameter.setValue(paramData.value);
            parameter.setDatePerformed(paramData.date);
            parameter.setTest(test);
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
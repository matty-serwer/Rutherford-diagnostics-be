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
        // Create Walter with multiple tests
        Patient walter = createPatient("Walter", "Dog", "Labrador", 
            LocalDate.of(2018, 5, 20), "Jane Doe", "555-1234");
        
        createTestForPatient(walter, "Complete Blood Count", LocalDate.of(2024, 3, 14), 
            "Hemoglobin", "g/dL", 12.0, 18.0, 14.5, 13.8, 15.2, 15.2, 13.1);
            
        createTestForPatient(walter, "Chemistry Panel", LocalDate.of(2024, 2, 14), 
            "Glucose", "mg/dL", 70.0, 140.0, 95.0, 88.0, 102.0, 91.5);
            
        createTestForPatient(walter, "Thyroid Panel", LocalDate.of(2024, 1, 14), 
            "T4", "ug/dL", 1.0, 4.0, 2.8, 2.5, 3.1, 2.9);

        // Create McGrupp with multiple tests
        Patient mcgrupp = createPatient("McGrupp", "Dog", "German Shepherd", 
            LocalDate.of(2019, 8, 15), "John Smith", "555-5678");
            
        createTestForPatient(mcgrupp, "Complete Blood Count", LocalDate.of(2024, 2, 20), 
            "Hemoglobin", "g/dL", 12.0, 18.0, 16.1, 15.8, 16.5, 15.9);
            
        createTestForPatient(mcgrupp, "Chemistry Panel", LocalDate.of(2024, 1, 20), 
            "Glucose", "mg/dL", 70.0, 140.0, 88.0, 92.0, 85.5, 90.0, 87.2);

        // Create Joan d'Bark with multiple tests
        Patient joan = createPatient("Joan d'Bark", "Dog", "Golden Retriever", 
            LocalDate.of(2020, 3, 10), "Alice Johnson", "555-9012");
            
        createTestForPatient(joan, "Complete Blood Count", LocalDate.of(2024, 1, 5), 
            "Hemoglobin", "g/dL", 12.0, 18.0, 13.2, 12.8, 13.5);
            
        createTestForPatient(joan, "Liver Function", LocalDate.of(2023, 12, 5), 
            "ALT", "U/L", 10.0, 80.0, 35.0, 28.0, 42.0, 31.5, 38.2, 33.1);
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

    private void createTestForPatient(Patient patient, String testName, LocalDate testDate, 
                                     String parameterName, String unit, Double referenceMin, Double referenceMax,
                                     Double... values) {
        // Create Test with shared parameter properties
        Test test = new Test();
        test.setName(testName);
        test.setDatePerformed(testDate);
        test.setParameterName(parameterName);
        test.setUnit(unit);
        test.setReferenceMin(referenceMin);
        test.setReferenceMax(referenceMax);
        test.setPatient(patient);
        test.setParameters(new ArrayList<>());
        test = testRepository.save(test);

        // Create Parameters with only values
        for (Double value : values) {
            Parameter parameter = new Parameter();
            parameter.setValue(value);
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
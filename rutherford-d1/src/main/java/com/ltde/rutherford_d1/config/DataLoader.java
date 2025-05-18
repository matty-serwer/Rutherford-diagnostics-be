package com.ltde.rutherford_d1.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.ResultHistory;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.repository.ParameterRepository;
import com.ltde.rutherford_d1.repository.PatientRepository;
import com.ltde.rutherford_d1.repository.ResultHistoryRepository;
import com.ltde.rutherford_d1.repository.TestRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final PatientRepository patientRepository;
    private final TestRepository testRepository;
    private final ParameterRepository parameterRepository;
    private final ResultHistoryRepository resultHistoryRepository;

    public DataLoader(PatientRepository patientRepository,
                      TestRepository testRepository,
                      ParameterRepository parameterRepository,
                      ResultHistoryRepository resultHistoryRepository) {
        this.patientRepository = patientRepository;
        this.testRepository = testRepository;
        this.parameterRepository = parameterRepository;
        this.resultHistoryRepository = resultHistoryRepository;
    }

    @Override
    public void run(String... args) {
        createPatientWithTests("Fido", "Dog", "Labrador", 
            LocalDate.of(2018, 5, 20), "Jane Doe", "555-1234",
            LocalDate.of(2024, 3, 14), new double[]{14.5, 13.8, 15.2, 15.2, 13.1, 14.2, 13.3, 13.4, 11.3, 12.0});

        createPatientWithTests("Luna", "Dog", "German Shepherd", 
            LocalDate.of(2019, 8, 15), "John Smith", "555-5678",
            LocalDate.of(2024, 2, 20), new double[]{16.1, 15.8, 14.9, 14.2, 15.5, 16.0, 15.7, 15.9, 14.8, 15.2});

        createPatientWithTests("Max", "Dog", "Golden Retriever", 
            LocalDate.of(2020, 3, 10), "Alice Johnson", "555-9012",
            LocalDate.of(2024, 1, 5), new double[]{13.2, 12.8, 13.5, 13.9, 14.1, 13.7, 13.4, 13.8, 13.2, 13.6});

        System.out.println("Data loading completed - Patient count: " + patientRepository.count());
    }

    private void createPatientWithTests(String name, String species, String breed, 
            LocalDate dob, String ownerName, String ownerContact,
            LocalDate testDate, double[] values) {
        // Create Patient
        Patient patient = new Patient();
        patient.setName(name);
        patient.setSpecies(species);
        patient.setBreed(breed);
        patient.setDateOfBirth(dob);
        patient.setOwnerName(ownerName);
        patient.setOwnerContact(ownerContact);
        patientRepository.save(patient);

        // Create Test
        Test test = new Test();
        test.setName("Blood Test");
        test.setDatePerformed(testDate);
        test.setPatient(patient);
        testRepository.save(test);

        // Create Parameter
        Parameter parameter = new Parameter();
        parameter.setName("Hemoglobin");
        parameter.setUnit("g/dL");
        parameter.setReferenceMin(12.0);
        parameter.setReferenceMax(18.0);
        parameter.setTest(test);
        parameterRepository.save(parameter);

        // Create history entries
        LocalDate historyDate = testDate;
        for (double value : values) {
            createResultHistory(parameter, historyDate, value);
            historyDate = historyDate.minusMonths(1);
        }
    }

    private void createResultHistory(Parameter parameter, LocalDate date, Double value) {
        ResultHistory result = new ResultHistory();
        result.setResultDate(date);
        result.setValue(value);
        result.setParameter(parameter);
        resultHistoryRepository.save(result);
    }
} 
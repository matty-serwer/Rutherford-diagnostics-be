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
        // Create Patient
        Patient patient = new Patient();
        patient.setName("Fido");
        patient.setSpecies("Dog");
        patient.setBreed("Labrador");
        patient.setDateOfBirth(LocalDate.of(2018, 5, 20));
        patient.setOwnerName("Jane Doe");
        patient.setOwnerContact("555-1234");
        patientRepository.save(patient);

        // Create Test
        Test test = new Test();
        test.setName("Blood Test");
        test.setDatePerformed(LocalDate.now());
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

        // Create ResultHistory
        ResultHistory result1 = new ResultHistory();
        result1.setResultDate(LocalDate.now().minusDays(1));
        result1.setValue(15.2);
        result1.setParameter(parameter);
        resultHistoryRepository.save(result1);

        ResultHistory result2 = new ResultHistory();
        result2.setResultDate(LocalDate.now());
        result2.setValue(16.1);
        result2.setParameter(parameter);
        resultHistoryRepository.save(result2);

        System.out.println("########################XXXXXXXXXXXXXXXXXXPatient count after seeding: " + patientRepository.count());
    }
} 
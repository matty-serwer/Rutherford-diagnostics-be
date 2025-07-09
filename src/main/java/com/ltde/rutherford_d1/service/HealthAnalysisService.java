package com.ltde.rutherford_d1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;

/**
 * Service for analyzing health status of parameters and calculating overall health scores
 */
@Service
public class HealthAnalysisService {

    /**
     * Calculate the health status of a parameter value based on reference ranges
     * @param value The measured parameter value
     * @param min The minimum reference range value
     * @param max The maximum reference range value
     * @return HealthStatus enum indicating the parameter's health status
     */
    public HealthStatus calculateParameterStatus(Double value, Double min, Double max) {
        // Handle null values
        if (value == null || min == null || max == null) {
            return HealthStatus.NORMAL; // Default to normal if no reference data
        }
        
        // Calculate percentage deviation from range
        double range = max - min;
        double criticalThreshold = range * 0.30; // 30% deviation threshold
        
        if (value >= min && value <= max) {
            return HealthStatus.NORMAL;
        } else if (value < min) {
            // Check if critically low (>30% below minimum)
            if (min - value > criticalThreshold) {
                return HealthStatus.CRITICAL;
            } else {
                return HealthStatus.LOW;
            }
        } else { // value > max
            // Check if critically high (>30% above maximum)
            if (value - max > criticalThreshold) {
                return HealthStatus.CRITICAL;
            } else {
                return HealthStatus.HIGH;
            }
        }
    }

    /**
     * Get all parameters with abnormal (non-normal) health status for a patient
     * @param patient The patient to analyze
     * @return List of parameters that are not in normal range
     */
    public List<Parameter> getAbnormalParameters(Patient patient) {
        return patient.getTests().stream()
            .flatMap(test -> test.getParameters().stream())
            .filter(parameter -> {
                // Calculate status if not already set
                if (parameter.getStatus() == null) {
                    Test test = parameter.getTest();
                    HealthStatus status = calculateParameterStatus(
                        parameter.getValue(), 
                        test.getReferenceMin(), 
                        test.getReferenceMax()
                    );
                    parameter.setStatus(status);
                }
                return parameter.getStatus() != HealthStatus.NORMAL;
            })
            .collect(Collectors.toList());
    }

    /**
     * Calculate an overall health score for a patient (0-100)
     * Higher scores indicate better health
     * @param patient The patient to calculate score for
     * @return Health score from 0 (worst) to 100 (best)
     */
    public int getHealthScore(Patient patient) {
        List<Parameter> allParameters = patient.getTests().stream()
            .flatMap(test -> test.getParameters().stream())
            .collect(Collectors.toList());
        
        if (allParameters.isEmpty()) {
            return 100; // No parameters means perfect health score
        }
        
        int totalPoints = 0;
        int maxPossiblePoints = allParameters.size() * 100; // Each parameter can contribute max 100 points
        
        for (Parameter parameter : allParameters) {
            // Calculate status if not already set
            if (parameter.getStatus() == null) {
                Test test = parameter.getTest();
                HealthStatus status = calculateParameterStatus(
                    parameter.getValue(), 
                    test.getReferenceMin(), 
                    test.getReferenceMax()
                );
                parameter.setStatus(status);
            }
            
            // Assign points based on health status
            switch (parameter.getStatus()) {
                case NORMAL:
                    totalPoints += 100; // Full points for normal
                    break;
                case LOW:
                case HIGH:
                    totalPoints += 60; // Moderate points for mild abnormalities
                    break;
                case CRITICAL:
                    totalPoints += 20; // Low points for critical abnormalities
                    break;
            }
        }
        
        // Calculate percentage score
        return (int) Math.round((double) totalPoints / maxPossiblePoints * 100);
    }
} 
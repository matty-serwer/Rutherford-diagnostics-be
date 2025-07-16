package com.ltde.rutherford_d1.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;

/**
 * Service for analyzing health status of parameters and calculating overall health scores
 * Implements core business logic for veterinary diagnostic analysis
 */
@Service
public class HealthAnalysisService {

    // Business rules configuration
    private static final double CRITICAL_LOW_THRESHOLD = 0.7;  // 70% of minimum
    private static final double CRITICAL_HIGH_THRESHOLD = 1.3; // 130% of maximum
    private static final int SCORE_DEDUCTION_LOW_HIGH = 5;     // Points deducted for LOW/HIGH
    private static final int SCORE_DEDUCTION_CRITICAL = 15;    // Points deducted for CRITICAL
    private static final int RECENCY_WEIGHT_DAYS = 90;         // Days to consider for recency weighting

    /**
     * Calculate the health status of a parameter value based on reference ranges
     * Uses precise business rules:
     * - NORMAL: min <= value <= max
     * - LOW: value < min (but > min * 0.7)
     * - HIGH: value > max (but < max * 1.3)  
     * - CRITICAL: value <= min * 0.7 OR value >= max * 1.3
     * 
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
        
        // Calculate critical thresholds
        double criticalLowThreshold = min * CRITICAL_LOW_THRESHOLD;   // 70% of min
        double criticalHighThreshold = max * CRITICAL_HIGH_THRESHOLD; // 130% of max
        
        // Apply business rules in order of severity
        if (value <= criticalLowThreshold || value >= criticalHighThreshold) {
            return HealthStatus.CRITICAL;
        } else if (value >= min && value <= max) {
            return HealthStatus.NORMAL;
        } else if (value < min) {
            return HealthStatus.LOW;
        } else { // value > max
            return HealthStatus.HIGH;
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
     * Calculate an overall health score for a patient (0-100) using business rules:
     * - Start at 100 (perfect health)
     * - Deduct 5 points for each LOW/HIGH parameter
     * - Deduct 15 points for each CRITICAL parameter
     * - Apply recency weighting (recent abnormals count more)
     * 
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
        
        int baseScore = 100; // Start with perfect score
        LocalDate now = LocalDate.now();
        
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
            
            // Skip normal parameters (no deduction)
            if (parameter.getStatus() == HealthStatus.NORMAL) {
                continue;
            }
            
            // Calculate base deduction
            int deduction = switch (parameter.getStatus()) {
                case LOW, HIGH -> SCORE_DEDUCTION_LOW_HIGH;     // 5 points
                case CRITICAL -> SCORE_DEDUCTION_CRITICAL;      // 15 points
                default -> 0;
            };
            
            // Apply recency weighting (recent abnormals count more)
            double recencyMultiplier = calculateRecencyMultiplier(parameter.getDatePerformed(), now);
            
            baseScore -= (int) Math.round(deduction * recencyMultiplier);
        }
        
        // Ensure score doesn't go below 0
        return Math.max(0, baseScore);
    }



    /**
     * Calculate recency multiplier for scoring
     * Recent abnormalities (within 90 days) get higher weighting
     * 
     * @param parameterDate The date of the parameter measurement
     * @param currentDate Current date for comparison
     * @return Multiplier between 1.0 (older) and 2.0 (very recent)
     */
    private double calculateRecencyMultiplier(LocalDate parameterDate, LocalDate currentDate) {
        if (parameterDate == null) {
            return 1.0; // Default multiplier for unknown dates
        }
        
        long daysSince = ChronoUnit.DAYS.between(parameterDate, currentDate);
        
        if (daysSince <= RECENCY_WEIGHT_DAYS) {
            // Linear decay from 2.0 (today) to 1.0 (90 days ago)
            return 2.0 - ((double) daysSince / RECENCY_WEIGHT_DAYS);
        } else {
            return 1.0; // Standard weight for older measurements
        }
    }


} 
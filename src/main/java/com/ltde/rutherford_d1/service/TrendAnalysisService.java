package com.ltde.rutherford_d1.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ltde.rutherford_d1.model.Parameter;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Test;
import com.ltde.rutherford_d1.model.Trend;

/**
 * Service dedicated to trending analysis and time-series evaluation
 * Implements business logic for determining health trends over time
 */
@Service
public class TrendAnalysisService {

    // Trend analysis configuration
    private static final double IMPROVEMENT_THRESHOLD = 0.1; // 10% change threshold
    private static final int MIN_DATA_POINTS = 2; // Minimum measurements needed for trending
    private static final int TREND_ANALYSIS_DAYS = 180; // Days to look back for trend analysis

    /**
     * Analyze trending for a specific parameter type in a test
     * Compares recent values to historical values to determine if health is improving or declining
     * 
     * @param test The test containing parameters to analyze
     * @return Trend indicating the direction of change
     */
    public Trend analyzeParameterTrend(Test test) {
        List<Parameter> parameters = test.getParameters().stream()
            .filter(param -> param.getDatePerformed() != null) // Only include dated parameters
            .filter(param -> param.getDatePerformed().isAfter(LocalDate.now().minusDays(TREND_ANALYSIS_DAYS)))
            .sorted(Comparator.comparing(Parameter::getDatePerformed))
            .collect(Collectors.toList());
        
        if (parameters.size() < MIN_DATA_POINTS) {
            return Trend.STABLE; // Need sufficient data points for trend analysis
        }
        
        // Split into recent and historical periods
        int splitPoint = Math.max(1, parameters.size() * 2 / 3); // Use last 1/3 as "recent"
        List<Parameter> historicalParams = parameters.subList(0, splitPoint);
        List<Parameter> recentParams = parameters.subList(splitPoint, parameters.size());
        
        if (historicalParams.isEmpty() || recentParams.isEmpty()) {
            return Trend.STABLE; // Need data in both periods
        }
        
        // Calculate average distance from normal range for both periods
        double recentDistanceFromNormal = calculateAverageDistanceFromNormal(recentParams, test);
        double historicalDistanceFromNormal = calculateAverageDistanceFromNormal(historicalParams, test);
        
        // Determine trend based on change in distance from normal
        double changeRatio = calculateChangeRatio(historicalDistanceFromNormal, recentDistanceFromNormal);
        
        if (changeRatio < -IMPROVEMENT_THRESHOLD) {
            return Trend.IMPROVING; // Getting closer to normal (distance decreased)
        } else if (changeRatio > IMPROVEMENT_THRESHOLD) {
            return Trend.DECLINING; // Moving away from normal (distance increased)
        } else {
            return Trend.STABLE; // No significant change
        }
    }

    /**
     * Get trending analysis for all parameter types for a patient
     * Groups parameters by test type and analyzes trends for each
     * 
     * @param patient The patient to analyze
     * @return Map of test identifiers to their trending direction
     */
    public Map<String, Trend> getPatientTrends(Patient patient) {
        return patient.getTests().stream()
            .collect(Collectors.toMap(
                test -> generateTestKey(test),
                test -> analyzeParameterTrend(test)
            ));
    }

    /**
     * Get the most recent parameter for a specific test type
     * Useful for current status display
     * 
     * @param test The test to get the latest parameter from
     * @return Optional containing the most recent parameter, or empty if none exist
     */
    public Optional<Parameter> getMostRecentParameter(Test test) {
        return test.getParameters().stream()
            .filter(param -> param.getDatePerformed() != null)
            .max(Comparator.comparing(Parameter::getDatePerformed));
    }

    /**
     * Analyze trends for parameters within a specific time window
     * Useful for focused analysis on recent health changes
     * 
     * @param test The test to analyze
     * @param daysPastToAnalyze Number of days to look back
     * @return Trend for the specified time period
     */
    public Trend analyzeRecentTrend(Test test, int daysPastToAnalyze) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysPastToAnalyze);
        
        List<Parameter> recentParameters = test.getParameters().stream()
            .filter(param -> param.getDatePerformed() != null)
            .filter(param -> param.getDatePerformed().isAfter(cutoffDate))
            .sorted(Comparator.comparing(Parameter::getDatePerformed))
            .collect(Collectors.toList());
        
        if (recentParameters.size() < MIN_DATA_POINTS) {
            return Trend.STABLE;
        }
        
        // Compare first half to second half of recent period
        int midPoint = recentParameters.size() / 2;
        List<Parameter> earlierRecent = recentParameters.subList(0, midPoint);
        List<Parameter> laterRecent = recentParameters.subList(midPoint, recentParameters.size());
        
        if (earlierRecent.isEmpty() || laterRecent.isEmpty()) {
            return Trend.STABLE;
        }
        
        double earlierDistance = calculateAverageDistanceFromNormal(earlierRecent, test);
        double laterDistance = calculateAverageDistanceFromNormal(laterRecent, test);
        
        double changeRatio = calculateChangeRatio(earlierDistance, laterDistance);
        
        if (changeRatio < -IMPROVEMENT_THRESHOLD) {
            return Trend.IMPROVING;
        } else if (changeRatio > IMPROVEMENT_THRESHOLD) {
            return Trend.DECLINING;
        } else {
            return Trend.STABLE;
        }
    }

    /**
     * Calculate the rate of improvement or decline over time
     * Returns a value indicating speed of change (higher = faster change)
     * 
     * @param test The test to analyze
     * @return Rate of change (positive = declining, negative = improving, 0 = stable)
     */
    public double calculateTrendVelocity(Test test) {
        List<Parameter> parameters = test.getParameters().stream()
            .filter(param -> param.getDatePerformed() != null)
            .sorted(Comparator.comparing(Parameter::getDatePerformed))
            .collect(Collectors.toList());
        
        if (parameters.size() < 3) {
            return 0.0; // Need at least 3 points for velocity calculation
        }
        
        // Calculate trend slope using linear regression on distance from normal
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        int n = parameters.size();
        
        for (int i = 0; i < n; i++) {
            Parameter param = parameters.get(i);
            double x = i; // Time index
            double y = calculateDistanceFromNormal(param, test); // Distance from normal
            
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }
        
        // Calculate slope (velocity)
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        return slope; // Positive = getting worse, negative = getting better
    }

    /**
     * Generate a unique key for a test for use in trend maps
     */
    private String generateTestKey(Test test) {
        return test.getName() + " - " + test.getParameterName();
    }

    /**
     * Calculate change ratio between two periods, handling edge cases
     */
    private double calculateChangeRatio(double historical, double recent) {
        if (Math.abs(historical) < 0.001) { // Avoid division by very small numbers
            return recent > 0.001 ? 1.0 : 0.0; // If was perfect, any change is significant
        }
        return (recent - historical) / Math.abs(historical);
    }

    /**
     * Calculate average distance from normal range for a list of parameters
     * Used in trend analysis to determine if values are getting closer or farther from normal
     * 
     * @param parameters List of parameters to analyze
     * @param test Test containing reference ranges
     * @return Average distance from normal range (0 = perfect, higher = worse)
     */
    private double calculateAverageDistanceFromNormal(List<Parameter> parameters, Test test) {
        if (parameters.isEmpty()) {
            return 0.0;
        }
        
        double totalDistance = 0.0;
        int validCount = 0;
        
        for (Parameter param : parameters) {
            double distance = calculateDistanceFromNormal(param, test);
            if (distance >= 0) { // Valid calculation
                totalDistance += distance;
                validCount++;
            }
        }
        
        return validCount > 0 ? totalDistance / validCount : 0.0;
    }

    /**
     * Calculate distance from normal range for a single parameter
     */
    private double calculateDistanceFromNormal(Parameter param, Test test) {
        Double value = param.getValue();
        Double min = test.getReferenceMin();
        Double max = test.getReferenceMax();
        
        if (value == null || min == null || max == null) {
            return -1.0; // Invalid data marker
        }
        
        // Calculate distance from normal range
        if (value >= min && value <= max) {
            return 0.0; // In normal range
        } else if (value < min) {
            return (min - value) / min; // Percentage below minimum
        } else {
            return (value - max) / max; // Percentage above maximum
        }
    }
} 
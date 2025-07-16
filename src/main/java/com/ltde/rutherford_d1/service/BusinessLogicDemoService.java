package com.ltde.rutherford_d1.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ltde.rutherford_d1.model.HealthStatus;
import com.ltde.rutherford_d1.model.Patient;
import com.ltde.rutherford_d1.model.Trend;

/**
 * Demonstration service showcasing the new business logic capabilities
 * This service provides examples of how the enhanced business rules work
 */
@Service
public class BusinessLogicDemoService {

    private final HealthAnalysisService healthAnalysisService;
    private final TrendAnalysisService trendAnalysisService;

    public BusinessLogicDemoService(HealthAnalysisService healthAnalysisService, TrendAnalysisService trendAnalysisService) {
        this.healthAnalysisService = healthAnalysisService;
        this.trendAnalysisService = trendAnalysisService;
    }

    /**
     * Demonstrate the new health status calculation rules
     * Shows examples of NORMAL, LOW, HIGH, and CRITICAL classifications
     */
    public void demonstrateHealthStatusRules() {
        System.out.println("=== PHASE 4 BUSINESS LOGIC DEMONSTRATION ===");
        System.out.println();
        
        // Example: Hemoglobin with reference range 12.0 - 18.0 g/dL
        double min = 12.0;
        double max = 18.0;
        
        System.out.println("Health Status Classification Rules (Hemoglobin 12.0-18.0 g/dL):");
        System.out.println("----------------------------------------------------------------");
        
        // Test various values
        testHealthStatus(15.0, min, max, "Perfect normal value");
        testHealthStatus(12.0, min, max, "Minimum normal value");
        testHealthStatus(18.0, min, max, "Maximum normal value");
        testHealthStatus(11.5, min, max, "Slightly low");
        testHealthStatus(8.4, min, max, "Critical low (70% of min = 8.4)");
        testHealthStatus(7.0, min, max, "Severely critical low");
        testHealthStatus(18.5, min, max, "Slightly high");
        testHealthStatus(23.4, min, max, "Critical high (130% of max = 23.4)");
        testHealthStatus(25.0, min, max, "Severely critical high");
        
        System.out.println();
    }

    /**
     * Demonstrate the new health scoring algorithm
     */
    public void demonstrateHealthScoring() {
        System.out.println("Health Scoring Algorithm:");
        System.out.println("- Start at 100 (perfect health)");
        System.out.println("- Deduct 5 points for each LOW/HIGH parameter");
        System.out.println("- Deduct 15 points for each CRITICAL parameter");
        System.out.println("- Apply recency weighting (recent abnormals count more)");
        System.out.println();
        
        // Example calculations
        System.out.println("Example Score Calculations:");
        System.out.println("- Patient with 10 normal parameters: 100 points");
        System.out.println("- Patient with 8 normal + 2 LOW: 100 - (2 × 5) = 90 points");
        System.out.println("- Patient with 7 normal + 2 HIGH + 1 CRITICAL: 100 - (2 × 5) - (1 × 15) = 75 points");
        System.out.println("- Recent abnormals (within 90 days) get up to 2x weight multiplier");
        System.out.println();
    }

    /**
     * Demonstrate trending analysis capabilities
     */
    public void demonstrateTrendingAnalysis() {
        System.out.println("Trending Analysis Business Logic:");
        System.out.println("- IMPROVING: Getting closer to normal range over time");
        System.out.println("- STABLE: No significant change (within 10% threshold)");
        System.out.println("- DECLINING: Moving away from normal range over time");
        System.out.println();
        
        System.out.println("Trend Analysis Features:");
        System.out.println("- Compares recent measurements (last 1/3) to historical data");
        System.out.println("- Uses distance from normal range as the key metric");
        System.out.println("- Requires minimum 2 data points for analysis");
        System.out.println("- Includes velocity calculation using linear regression");
        System.out.println("- Supports custom time windows for focused analysis");
        System.out.println();
    }

    /**
     * Show real-world example using demo data
     */
    public String analyzePatientExample(Patient patient) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("=== PATIENT ANALYSIS: ").append(patient.getName()).append(" ===\n");
        
        // Health Score
        int healthScore = healthAnalysisService.getHealthScore(patient);
        analysis.append("Health Score: ").append(healthScore).append("/100\n");
        
        // Abnormal Parameters Count
        int abnormalCount = healthAnalysisService.getAbnormalParameters(patient).size();
        analysis.append("Abnormal Parameters: ").append(abnormalCount).append("\n");
        
        // Trend Analysis
        Map<String, Trend> trends = trendAnalysisService.getPatientTrends(patient);
        analysis.append("Trending Analysis:\n");
        trends.forEach((testName, trend) -> 
            analysis.append("  - ").append(testName).append(": ").append(trend).append("\n")
        );
        
        // Health Assessment
        analysis.append("Assessment: ");
        if (healthScore >= 90) {
            analysis.append("Excellent health - all parameters within normal ranges\n");
        } else if (healthScore >= 75) {
            analysis.append("Good health - minor abnormalities present\n");
        } else if (healthScore >= 60) {
            analysis.append("Moderate health concerns - multiple abnormalities\n");
        } else {
            analysis.append("Significant health issues - immediate attention recommended\n");
        }
        
        return analysis.toString();
    }

    /**
     * Helper method to test and display health status calculations
     */
    private void testHealthStatus(double value, double min, double max, String description) {
        HealthStatus status = healthAnalysisService.calculateParameterStatus(value, min, max);
        System.out.printf("Value: %5.1f g/dL → %-8s (%s)%n", value, status, description);
    }
} 
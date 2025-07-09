package com.ltde.rutherford_d1.dto;

/**
 * DTO for providing a summary of health status information
 * Used for dashboard views and health overview displays
 */
public record HealthSummaryDTO(
    int healthScore,           // Overall health score (0-100)
    int totalParameters,       // Total number of parameters measured
    int normalCount,          // Count of normal parameters
    int lowCount,             // Count of low parameters
    int highCount,            // Count of high parameters  
    int criticalCount,        // Count of critical parameters
    int abnormalCount         // Total count of abnormal parameters (low + high + critical)
) {} 
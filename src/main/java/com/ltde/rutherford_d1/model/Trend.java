package com.ltde.rutherford_d1.model;

/**
 * Enum representing the trend direction of a parameter over time
 * Used for analyzing whether patient health is improving or declining
 */
public enum Trend {
    IMPROVING,    // Getting closer to normal range
    STABLE,       // No significant change
    DECLINING     // Moving away from normal range
} 
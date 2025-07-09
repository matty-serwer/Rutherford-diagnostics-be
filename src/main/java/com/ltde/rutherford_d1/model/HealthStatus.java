package com.ltde.rutherford_d1.model;

/**
 * Enum representing the health status of a parameter measurement
 * compared to its reference range
 */
public enum HealthStatus {
    NORMAL,     // Within reference range
    LOW,        // Below reference range
    HIGH,       // Above reference range
    CRITICAL    // Significantly outside range (>30% deviation)
} 
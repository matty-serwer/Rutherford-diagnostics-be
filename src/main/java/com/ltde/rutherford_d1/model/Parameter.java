package com.ltde.rutherford_d1.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Parameter {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double value; // The measurement value
    private LocalDate datePerformed; // When this specific measurement was taken

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
} 
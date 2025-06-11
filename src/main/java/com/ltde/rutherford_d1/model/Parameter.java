package com.ltde.rutherford_d1.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Parameter {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String unit;
    private Double referenceMin;
    private Double referenceMax;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL)
    private List<ResultHistory> history;
} 
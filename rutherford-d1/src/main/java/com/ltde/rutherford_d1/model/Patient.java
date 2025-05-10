package com.ltde.rutherford_d1.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Patient {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String species;
    private String breed;
    private LocalDate dateOfBirth;
    private String ownerName;
    private String ownerContact;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Test> tests;
} 
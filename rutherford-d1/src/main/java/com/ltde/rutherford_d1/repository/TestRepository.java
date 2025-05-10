package com.ltde.rutherford_d1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ltde.rutherford_d1.model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {} 
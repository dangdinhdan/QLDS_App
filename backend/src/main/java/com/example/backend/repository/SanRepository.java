package com.example.backend.repository;

import com.example.backend.model.San;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanRepository extends JpaRepository<San, Integer> {
}

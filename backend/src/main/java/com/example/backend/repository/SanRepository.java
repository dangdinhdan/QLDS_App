package com.example.backend.repository;

import com.example.backend.model.San;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SanRepository extends JpaRepository<San, Integer> {
    Optional<San> findByMaSan(String maSan);
}


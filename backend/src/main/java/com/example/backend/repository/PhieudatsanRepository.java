package com.example.backend.repository;

import com.example.backend.model.Phieudatsan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieudatsanRepository extends JpaRepository<Phieudatsan, Integer> {
}

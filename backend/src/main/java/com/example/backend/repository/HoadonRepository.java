package com.example.backend.repository;

import com.example.backend.model.Hoadon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoadonRepository extends JpaRepository<Hoadon, Integer> {
    Optional<Hoadon> findByPhieudatsanId(Integer phieuId);
}

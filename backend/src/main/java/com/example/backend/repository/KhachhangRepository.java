package com.example.backend.repository;

import com.example.backend.model.Khachhang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachhangRepository extends JpaRepository<Khachhang, Integer> {
    Optional<Khachhang> findFirstBySdt(String sdt);
    Optional<Khachhang> findFirstByTen(String ten);
}

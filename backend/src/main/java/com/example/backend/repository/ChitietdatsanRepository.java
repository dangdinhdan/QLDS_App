package com.example.backend.repository;

import com.example.backend.model.Chitietdatsan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChitietdatsanRepository extends JpaRepository<Chitietdatsan, Integer> {
    List<Chitietdatsan> findByPhieudatsanId(Integer phieudatsanId);
}

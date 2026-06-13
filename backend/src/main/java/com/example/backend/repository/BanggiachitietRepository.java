package com.example.backend.repository;

import com.example.backend.model.Banggiachitiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanggiachitietRepository extends JpaRepository<Banggiachitiet, Integer> {
}

package com.example.backend.repository;

import com.example.backend.model.Banggia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BanggiaRepository extends JpaRepository<Banggia, Integer> {
    List<Banggia> findByIsdeleteFalse();
}

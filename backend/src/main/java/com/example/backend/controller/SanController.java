package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.model.San;
import com.example.backend.repository.SanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san")
public class SanController {

    @Autowired
    private SanRepository sanRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<San>>> getAllSan() {
        try {
            List<San> listSan = sanRepository.findAll();
            return ResponseEntity.ok(ApiResponse.<List<San>>builder()
                    .success(true)
                    .message("Lấy danh sách sân thành công!")
                    .data(listSan)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<List<San>>builder()
                    .success(false)
                    .message("Lỗi khi lấy danh sách sân: " + e.getMessage())
                    .build());
        }
    }
}

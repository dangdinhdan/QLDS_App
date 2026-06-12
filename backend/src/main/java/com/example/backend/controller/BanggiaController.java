package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.model.Banggia;
import com.example.backend.repository.BanggiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banggia")
public class BanggiaController {

    @Autowired
    private BanggiaRepository banggiaRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Banggia>>> getAllBanggia() {
        try {
            List<Banggia> listBanggia = banggiaRepository.findByIsdeleteFalse();
            return ResponseEntity.ok(ApiResponse.<List<Banggia>>builder()
                    .success(true)
                    .message("Lấy danh sách bảng giá thành công!")
                    .data(listBanggia)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<List<Banggia>>builder()
                    .success(false)
                    .message("Lỗi khi lấy danh sách bảng giá: " + e.getMessage())
                    .build());
        }
    }
}

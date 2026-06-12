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
            List<San> listSan = sanRepository.findByIsdeleteFalse();
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

    @PostMapping
    public ResponseEntity<ApiResponse<San>> createSan(@RequestBody San san) {
        try {
            if (san.getMaSan() == null || san.getMaSan().trim().isEmpty() ||
                san.getTen() == null || san.getTen().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.<San>builder()
                        .success(false)
                        .message("Mã sân và tên sân không được để trống!")
                        .build());
            }

            if (sanRepository.findByMaSan(san.getMaSan()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.<San>builder()
                        .success(false)
                        .message("Mã sân đã tồn tại!")
                        .build());
            }

            San savedSan = sanRepository.save(san);
            return ResponseEntity.ok(ApiResponse.<San>builder()
                    .success(true)
                    .message("Thêm sân mới thành công!")
                    .data(savedSan)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<San>builder()
                    .success(false)
                    .message("Lỗi khi thêm sân mới: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<San>> updateSan(@PathVariable Integer id, @RequestBody San sanDetails) {
        try {
            return sanRepository.findById(id).map(san -> {
                san.setMaSan(sanDetails.getMaSan());
                san.setTen(sanDetails.getTen());
                san.setLoaimatsan(sanDetails.getLoaimatsan());
                san.setTrangthai(sanDetails.getTrangthai());
                san.setIdBanggia(sanDetails.getIdBanggia());
                if (sanDetails.getUrl() != null && !sanDetails.getUrl().trim().isEmpty()) {
                    san.setUrl(sanDetails.getUrl());
                }
                San updatedSan = sanRepository.save(san);
                return ResponseEntity.ok(ApiResponse.<San>builder()
                        .success(true)
                        .message("Cập nhật thông tin sân thành công!")
                        .data(updatedSan)
                        .build());
            }).orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.<San>builder()
                    .success(false)
                    .message("Không tìm thấy sân với ID: " + id)
                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<San>builder()
                    .success(false)
                    .message("Lỗi khi cập nhật thông tin sân: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSan(@PathVariable Integer id) {
        try {
            return sanRepository.findById(id).map(san -> {
                san.setIsdelete(true);
                sanRepository.save(san);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa sân thành công!")
                        .build());
            }).orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Không tìm thấy sân với ID: " + id)
                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Lỗi khi xóa sân: " + e.getMessage())
                    .build());
        }
    }
}


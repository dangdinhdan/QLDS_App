package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.model.Banggia;
import com.example.backend.model.Banggiachitiet;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Banggia>> createBanggia(@RequestBody Banggia banggia) {
        try {
            banggia.setId(null);
            banggia.setIsdelete(false);
            if (banggia.getDetails() != null) {
                for (Banggiachitiet detail : banggia.getDetails()) {
                    detail.setBanggia(banggia);
                }
            }
            Banggia saved = banggiaRepository.save(banggia);
            return ResponseEntity.ok(ApiResponse.<Banggia>builder()
                    .success(true)
                    .message("Thêm bảng giá thành công!")
                    .data(saved)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Banggia>builder()
                    .success(false)
                    .message("Lỗi khi thêm bảng giá: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Banggia>> updateBanggia(@PathVariable Integer id, @RequestBody Banggia details) {
        try {
            return banggiaRepository.findById(id).map(banggia -> {
                banggia.setMaBanggia(details.getMaBanggia());
                banggia.setTenbanggia(details.getTenbanggia());
                banggia.setMota(details.getMota());
                
                // Clear old details and set parent for new details to trigger cascade updates
                if (banggia.getDetails() != null) {
                    banggia.getDetails().clear();
                    if (details.getDetails() != null) {
                        for (Banggiachitiet newDetail : details.getDetails()) {
                            newDetail.setId(null);
                            newDetail.setBanggia(banggia);
                            banggia.getDetails().add(newDetail);
                        }
                    }
                } else if (details.getDetails() != null) {
                    java.util.List<Banggiachitiet> detailsList = new java.util.ArrayList<>();
                    for (Banggiachitiet newDetail : details.getDetails()) {
                        newDetail.setId(null);
                        newDetail.setBanggia(banggia);
                        detailsList.add(newDetail);
                    }
                    banggia.setDetails(detailsList);
                }
                
                Banggia saved = banggiaRepository.save(banggia);
                return ResponseEntity.ok(ApiResponse.<Banggia>builder()
                        .success(true)
                        .message("Cập nhật bảng giá thành công!")
                        .data(saved)
                        .build());
            }).orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.<Banggia>builder()
                    .success(false)
                    .message("Không tìm thấy bảng giá với ID: " + id)
                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Banggia>builder()
                    .success(false)
                    .message("Lỗi khi cập nhật bảng giá: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanggia(@PathVariable Integer id) {
        try {
            return banggiaRepository.findById(id).map(banggia -> {
                banggia.setIsdelete(true);
                banggiaRepository.save(banggia);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa bảng giá thành công!")
                        .build());
            }).orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Không tìm thấy bảng giá với ID: " + id)
                        .build()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Lỗi khi xóa bảng giá: " + e.getMessage())
                    .build());
        }
    }
}

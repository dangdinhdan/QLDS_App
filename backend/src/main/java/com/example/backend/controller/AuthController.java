package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.model.TaiKhoan;
import com.example.backend.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản và mật khẩu không được để trống!")
                    .build());
        }

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTaikhoan(request.getUsername());

        if (userOpt.isPresent()) {
            TaiKhoan user = userOpt.get();
            // Compare plain text password
            if (user.getMatkhau().equals(request.getPassword())) {
                return ResponseEntity.ok(LoginResponse.builder()
                        .success(true)
                        .message("Đăng nhập thành công!")
                        .id(user.getId())
                        .username(user.getTaikhoan())
                        .build());
            }
        }

        return ResponseEntity.status(401).body(LoginResponse.builder()
                .success(false)
                .message("Tài khoản hoặc mật khẩu không chính xác!")
                .build());
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API Authentication is working fine!");
    }
}

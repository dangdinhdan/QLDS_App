package com.example.backend.model;

import java.time.LocalDateTime;

public class TaiKhoan {

    private Integer id;
    private String taikhoan;
    private String matkhau;
    private LocalDateTime createAt;

    public TaiKhoan() {
    }

    public TaiKhoan(Integer id, String taikhoan, String matkhau, LocalDateTime createAt) {
        this.id = id;
        this.taikhoan = taikhoan;
        this.matkhau = matkhau;
        this.createAt = createAt;
    }

    public static TaiKhoanBuilder builder() {
        return new TaiKhoanBuilder();
    }

    public static class TaiKhoanBuilder {
        private Integer id;
        private String taikhoan;
        private String matkhau;
        private LocalDateTime createAt;

        public TaiKhoanBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public TaiKhoanBuilder taikhoan(String taikhoan) {
            this.taikhoan = taikhoan;
            return this;
        }

        public TaiKhoanBuilder matkhau(String matkhau) {
            this.matkhau = matkhau;
            return this;
        }

        public TaiKhoanBuilder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public TaiKhoan build() {
            return new TaiKhoan(id, taikhoan, matkhau, createAt);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaikhoan() {
        return taikhoan;
    }

    public void setTaikhoan(String taikhoan) {
        this.taikhoan = taikhoan;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}

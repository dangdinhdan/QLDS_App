package com.example.backend.model;

import java.time.LocalDateTime;

public class Khachhang {

    private Integer id;
    private String maKh;
    private String ten;
    private String sdt;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public Khachhang() {
    }

    public Khachhang(Integer id, String maKh, String ten, String sdt, LocalDateTime createAt, LocalDateTime updateAt) {
        this.id = id;
        this.maKh = maKh;
        this.ten = ten;
        this.sdt = sdt;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public static KhachhangBuilder builder() {
        return new KhachhangBuilder();
    }

    public static class KhachhangBuilder {
        private Integer id;
        private String maKh;
        private String ten;
        private String sdt;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;

        public KhachhangBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public KhachhangBuilder maKh(String maKh) {
            this.maKh = maKh;
            return this;
        }

        public KhachhangBuilder ten(String ten) {
            this.ten = ten;
            return this;
        }

        public KhachhangBuilder sdt(String sdt) {
            this.sdt = sdt;
            return this;
        }

        public KhachhangBuilder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public KhachhangBuilder updateAt(LocalDateTime updateAt) {
            this.updateAt = updateAt;
            return this;
        }

        public Khachhang build() {
            return new Khachhang(id, maKh, ten, sdt, createAt, updateAt);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaKh() {
        return maKh;
    }

    public void setMaKh(String maKh) {
        this.maKh = maKh;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}

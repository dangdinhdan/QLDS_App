package com.example.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Phieudatsan {

    private Integer id;
    private Khachhang khachhang;
    private LocalDate ngaydat;
    private String trangthai;
    private String ghichu;
    private LocalDateTime createAt;
    private List<Chitietdatsan> details;

    public Phieudatsan() {
    }

    public Phieudatsan(Integer id, Khachhang khachhang, LocalDate ngaydat, String trangthai, String ghichu, LocalDateTime createAt, List<Chitietdatsan> details) {
        this.id = id;
        this.khachhang = khachhang;
        this.ngaydat = ngaydat;
        this.trangthai = trangthai;
        this.ghichu = ghichu;
        this.createAt = createAt;
        this.details = details;
    }

    public static PhieudatsanBuilder builder() {
        return new PhieudatsanBuilder();
    }

    public static class PhieudatsanBuilder {
        private Integer id;
        private Khachhang khachhang;
        private LocalDate ngaydat;
        private String trangthai;
        private String ghichu;
        private LocalDateTime createAt;
        private List<Chitietdatsan> details;

        public PhieudatsanBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public PhieudatsanBuilder khachhang(Khachhang khachhang) {
            this.khachhang = khachhang;
            return this;
        }

        public PhieudatsanBuilder ngaydat(LocalDate ngaydat) {
            this.ngaydat = ngaydat;
            return this;
        }

        public PhieudatsanBuilder trangthai(String trangthai) {
            this.trangthai = trangthai;
            return this;
        }

        public PhieudatsanBuilder ghichu(String ghichu) {
            this.ghichu = ghichu;
            return this;
        }

        public PhieudatsanBuilder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public PhieudatsanBuilder details(List<Chitietdatsan> details) {
            this.details = details;
            return this;
        }

        public Phieudatsan build() {
            return new Phieudatsan(id, khachhang, ngaydat, trangthai, ghichu, createAt, details);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Khachhang getKhachhang() {
        return khachhang;
    }

    public void setKhachhang(Khachhang khachhang) {
        this.khachhang = khachhang;
    }

    public LocalDate getNgaydat() {
        return ngaydat;
    }

    public void setNgaydat(LocalDate ngaydat) {
        this.ngaydat = ngaydat;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<Chitietdatsan> getDetails() {
        return details;
    }

    public void setDetails(List<Chitietdatsan> details) {
        this.details = details;
    }
}

package com.example.backend.model;

import java.time.LocalDateTime;

public class San {

    private Integer id;
    private String maSan;
    private Integer idBanggia;
    private String ten;
    private String loaimatsan;
    private String trangthai;
    private String url;
    private LocalDateTime createAt;
    private LocalDateTime deleteAt;
    private Boolean isdelete = false;

    public San() {
    }

    public San(Integer id, String maSan, Integer idBanggia, String ten, String loaimatsan, String trangthai, String url, LocalDateTime createAt, LocalDateTime deleteAt, Boolean isdelete) {
        this.id = id;
        this.maSan = maSan;
        this.idBanggia = idBanggia;
        this.ten = ten;
        this.loaimatsan = loaimatsan;
        this.trangthai = trangthai;
        this.url = url;
        this.createAt = createAt;
        this.deleteAt = deleteAt;
        this.isdelete = isdelete;
    }

    public static SanBuilder builder() {
        return new SanBuilder();
    }

    public static class SanBuilder {
        private Integer id;
        private String maSan;
        private Integer idBanggia;
        private String ten;
        private String loaimatsan;
        private String trangthai;
        private String url;
        private LocalDateTime createAt;
        private LocalDateTime deleteAt;
        private Boolean isdelete = false;

        public SanBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public SanBuilder maSan(String maSan) {
            this.maSan = maSan;
            return this;
        }

        public SanBuilder idBanggia(Integer idBanggia) {
            this.idBanggia = idBanggia;
            return this;
        }

        public SanBuilder ten(String ten) {
            this.ten = ten;
            return this;
        }

        public SanBuilder loaimatsan(String loaimatsan) {
            this.loaimatsan = loaimatsan;
            return this;
        }

        public SanBuilder trangthai(String trangthai) {
            this.trangthai = trangthai;
            return this;
        }

        public SanBuilder url(String url) {
            this.url = url;
            return this;
        }

        public SanBuilder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public SanBuilder deleteAt(LocalDateTime deleteAt) {
            this.deleteAt = deleteAt;
            return this;
        }

        public SanBuilder isdelete(Boolean isdelete) {
            this.isdelete = isdelete;
            return this;
        }

        public San build() {
            return new San(id, maSan, idBanggia, ten, loaimatsan, trangthai, url, createAt, deleteAt, isdelete);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaSan() {
        return maSan;
    }

    public void setMaSan(String maSan) {
        this.maSan = maSan;
    }

    public Integer getIdBanggia() {
        return idBanggia;
    }

    public void setIdBanggia(Integer idBanggia) {
        this.idBanggia = idBanggia;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getLoaimatsan() {
        return loaimatsan;
    }

    public void setLoaimatsan(String loaimatsan) {
        this.loaimatsan = loaimatsan;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(LocalDateTime deleteAt) {
        this.deleteAt = deleteAt;
    }

    public Boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Boolean isdelete) {
        this.isdelete = isdelete;
    }
}

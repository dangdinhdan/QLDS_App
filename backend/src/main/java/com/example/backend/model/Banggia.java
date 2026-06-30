package com.example.backend.model;

import java.time.LocalDateTime;

public class Banggia {

    private Integer id;
    private String maBanggia;
    private String tenbanggia;
    private String mota;
    private java.util.List<Banggiachitiet> details;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;
    private Boolean isdelete = false;

    public Banggia() {
    }

    public Banggia(Integer id, String maBanggia, String tenbanggia, String mota, java.util.List<Banggiachitiet> details, LocalDateTime createAt, LocalDateTime updateAt, LocalDateTime deleteAt, Boolean isdelete) {
        this.id = id;
        this.maBanggia = maBanggia;
        this.tenbanggia = tenbanggia;
        this.mota = mota;
        this.details = details;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.deleteAt = deleteAt;
        this.isdelete = isdelete;
    }

    public static BanggiaBuilder builder() {
        return new BanggiaBuilder();
    }

    public static class BanggiaBuilder {
        private Integer id;
        private String maBanggia;
        private String tenbanggia;
        private String mota;
        private java.util.List<Banggiachitiet> details;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;
        private LocalDateTime deleteAt;
        private Boolean isdelete = false;

        public BanggiaBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public BanggiaBuilder maBanggia(String maBanggia) {
            this.maBanggia = maBanggia;
            return this;
        }

        public BanggiaBuilder tenbanggia(String tenbanggia) {
            this.tenbanggia = tenbanggia;
            return this;
        }

        public BanggiaBuilder mota(String mota) {
            this.mota = mota;
            return this;
        }

        public BanggiaBuilder details(java.util.List<Banggiachitiet> details) {
            this.details = details;
            return this;
        }

        public BanggiaBuilder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public BanggiaBuilder updateAt(LocalDateTime updateAt) {
            this.updateAt = updateAt;
            return this;
        }

        public BanggiaBuilder deleteAt(LocalDateTime deleteAt) {
            this.deleteAt = deleteAt;
            return this;
        }

        public BanggiaBuilder isdelete(Boolean isdelete) {
            this.isdelete = isdelete;
            return this;
        }

        public Banggia build() {
            return new Banggia(id, maBanggia, tenbanggia, mota, details, createAt, updateAt, deleteAt, isdelete);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaBanggia() {
        return maBanggia;
    }

    public void setMaBanggia(String maBanggia) {
        this.maBanggia = maBanggia;
    }

    public String getTenbanggia() {
        return tenbanggia;
    }

    public void setTenbanggia(String tenbanggia) {
        this.tenbanggia = tenbanggia;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public java.util.List<Banggiachitiet> getDetails() {
        return details;
    }

    public void setDetails(java.util.List<Banggiachitiet> details) {
        this.details = details;
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

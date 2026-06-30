package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Banggiachitiet {

    private Integer id;
    private Banggia banggia;
    private String loaingay;
    private LocalTime giobatdau;
    private LocalTime giokethuc;
    private Double dongia;
    private LocalDateTime createAt;

    public Banggiachitiet() {
    }

    public Banggiachitiet(Integer id, Banggia banggia, String loaingay, LocalTime giobatdau, LocalTime giokethuc, Double dongia, LocalDateTime createAt) {
        this.id = id;
        this.banggia = banggia;
        this.loaingay = loaingay;
        this.giobatdau = giobatdau;
        this.giokethuc = giokethuc;
        this.dongia = dongia;
        this.createAt = createAt;
    }

    public static BanggiachitietBuilder builder() {
        return new BanggiachitietBuilder();
    }

    public static class BanggiachitietBuilder {
        private Integer id;
        private Banggia banggia;
        private String loaingay;
        private LocalTime giobatdau;
        private LocalTime giokethuc;
        private Double dongia;
        private LocalDateTime createAt;

        public BanggiachitietBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public BanggiachitietBuilder banggia(Banggia banggia) {
            this.banggia = banggia;
            return this;
        }

        public BanggiachitietBuilder loaingay(String loaingay) {
            this.loaingay = loaingay;
            return this;
        }

        public BanggiachitietBuilder giobatdau(LocalTime giobatdau) {
            this.giobatdau = giobatdau;
            return this;
        }

        public BanggiachitietBuilder giokethuc(LocalTime giokethuc) {
            this.giokethuc = giokethuc;
            return this;
        }

        public BanggiachitietBuilder dongia(Double dongia) {
            this.dongia = dongia;
            return this;
        }

        public BanggiachitietBuilder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public Banggiachitiet build() {
            return new Banggiachitiet(id, banggia, loaingay, giobatdau, giokethuc, dongia, createAt);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Banggia getBanggia() {
        return banggia;
    }

    public void setBanggia(Banggia banggia) {
        this.banggia = banggia;
    }

    public String getLoaingay() {
        return loaingay;
    }

    public void setLoaingay(String loaingay) {
        this.loaingay = loaingay;
    }

    public LocalTime getGiobatdau() {
        return giobatdau;
    }

    public void setGiobatdau(LocalTime giobatdau) {
        this.giobatdau = giobatdau;
    }

    public LocalTime getGiokethuc() {
        return giokethuc;
    }

    public void setGiokethuc(LocalTime giokethuc) {
        this.giokethuc = giokethuc;
    }

    public Double getDongia() {
        return dongia;
    }

    public void setDongia(Double dongia) {
        this.dongia = dongia;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}

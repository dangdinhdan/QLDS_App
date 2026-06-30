package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalTime;

public class Chitietdatsan {

    private Integer id;
    @JsonIgnore
    private Phieudatsan phieudatsan;
    private San san;
    private LocalTime giobatdau;
    private LocalTime giokethuc;
    private Double dongia;
    private Double thanhtien;

    public Chitietdatsan() {
    }

    public Chitietdatsan(Integer id, Phieudatsan phieudatsan, San san, LocalTime giobatdau, LocalTime giokethuc, Double dongia, Double thanhtien) {
        this.id = id;
        this.phieudatsan = phieudatsan;
        this.san = san;
        this.giobatdau = giobatdau;
        this.giokethuc = giokethuc;
        this.dongia = dongia;
        this.thanhtien = thanhtien;
    }

    public static ChitietdatsanBuilder builder() {
        return new ChitietdatsanBuilder();
    }

    public static class ChitietdatsanBuilder {
        private Integer id;
        private Phieudatsan phieudatsan;
        private San san;
        private LocalTime giobatdau;
        private LocalTime giokethuc;
        private Double dongia;
        private Double thanhtien;

        public ChitietdatsanBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public ChitietdatsanBuilder phieudatsan(Phieudatsan phieudatsan) {
            this.phieudatsan = phieudatsan;
            return this;
        }

        public ChitietdatsanBuilder san(San san) {
            this.san = san;
            return this;
        }

        public ChitietdatsanBuilder giobatdau(LocalTime giobatdau) {
            this.giobatdau = giobatdau;
            return this;
        }

        public ChitietdatsanBuilder giokethuc(LocalTime giokethuc) {
            this.giokethuc = giokethuc;
            return this;
        }

        public ChitietdatsanBuilder dongia(Double dongia) {
            this.dongia = dongia;
            return this;
        }

        public ChitietdatsanBuilder thanhtien(Double thanhtien) {
            this.thanhtien = thanhtien;
            return this;
        }

        public Chitietdatsan build() {
            return new Chitietdatsan(id, phieudatsan, san, giobatdau, giokethuc, dongia, thanhtien);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Phieudatsan getPhieudatsan() {
        return phieudatsan;
    }

    public void setPhieudatsan(Phieudatsan phieudatsan) {
        this.phieudatsan = phieudatsan;
    }

    public San getSan() {
        return san;
    }

    public void setSan(San san) {
        this.san = san;
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

    public Double getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(Double thanhtien) {
        this.thanhtien = thanhtien;
    }
}

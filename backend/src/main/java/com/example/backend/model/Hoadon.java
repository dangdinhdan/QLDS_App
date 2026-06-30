package com.example.backend.model;

public class Hoadon {

    private Integer id;
    private String maHoadon;
    private Phieudatsan phieudatsan;
    private String ghichu;
    private Double tongtien;

    public Hoadon() {
    }

    public Hoadon(Integer id, String maHoadon, Phieudatsan phieudatsan, String ghichu, Double tongtien) {
        this.id = id;
        this.maHoadon = maHoadon;
        this.phieudatsan = phieudatsan;
        this.ghichu = ghichu;
        this.tongtien = tongtien;
    }

    public static HoadonBuilder builder() {
        return new HoadonBuilder();
    }

    public static class HoadonBuilder {
        private Integer id;
        private String maHoadon;
        private Phieudatsan phieudatsan;
        private String ghichu;
        private Double tongtien;

        public HoadonBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public HoadonBuilder maHoadon(String maHoadon) {
            this.maHoadon = maHoadon;
            return this;
        }

        public HoadonBuilder phieudatsan(Phieudatsan phieudatsan) {
            this.phieudatsan = phieudatsan;
            return this;
        }

        public HoadonBuilder ghichu(String ghichu) {
            this.ghichu = ghichu;
            return this;
        }

        public HoadonBuilder tongtien(Double tongtien) {
            this.tongtien = tongtien;
            return this;
        }

        public Hoadon build() {
            return new Hoadon(id, maHoadon, phieudatsan, ghichu, tongtien);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaHoadon() {
        return maHoadon;
    }

    public void setMaHoadon(String maHoadon) {
        this.maHoadon = maHoadon;
    }

    public Phieudatsan getPhieudatsan() {
        return phieudatsan;
    }

    public void setPhieudatsan(Phieudatsan phieudatsan) {
        this.phieudatsan = phieudatsan;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public Double getTongtien() {
        return tongtien;
    }

    public void setTongtien(Double tongtien) {
        this.tongtien = tongtien;
    }
}

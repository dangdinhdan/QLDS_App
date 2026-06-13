package com.example.mobile.model;

public class PriceSlot {
    private final int id;
    private final String loaingay;
    private final String giobatdau;
    private final String giokethuc;
    private final double dongia;

    public PriceSlot(int id, String loaingay, String giobatdau, String giokethuc, double dongia) {
        this.id = id;
        this.loaingay = loaingay;
        this.giobatdau = giobatdau;
        this.giokethuc = giokethuc;
        this.dongia = dongia;
    }

    public int getId() {
        return id;
    }

    public String getLoaingay() {
        return loaingay;
    }

    public String getGiobatdau() {
        return giobatdau;
    }

    public String getGiokethuc() {
        return giokethuc;
    }

    public double getDongia() {
        return dongia;
    }
}

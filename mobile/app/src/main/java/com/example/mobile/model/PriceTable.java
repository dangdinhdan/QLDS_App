package com.example.mobile.model;

public class PriceTable {
    private final int id;
    private final String maBanggia;
    private final String tenbanggia;
    private final String mota;

    public PriceTable(int id, String maBanggia, String tenbanggia, String mota) {
        this.id = id;
        this.maBanggia = maBanggia;
        this.tenbanggia = tenbanggia;
        this.mota = mota;
    }

    public int getId() {
        return id;
    }

    public String getMaBanggia() {
        return maBanggia;
    }

    public String getTenbanggia() {
        return tenbanggia;
    }

    public String getMota() {
        return mota;
    }
}

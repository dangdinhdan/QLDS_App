package com.example.mobile.model;

import java.util.ArrayList;
import java.util.List;

public class PriceTable {
    private final int id;
    private final String maBanggia;
    private final String tenbanggia;
    private final String mota;
    private final List<PriceSlot> details;

    public PriceTable(int id, String maBanggia, String tenbanggia, String mota) {
        this(id, maBanggia, tenbanggia, mota, new ArrayList<>());
    }

    public PriceTable(int id, String maBanggia, String tenbanggia, String mota, List<PriceSlot> details) {
        this.id = id;
        this.maBanggia = maBanggia;
        this.tenbanggia = tenbanggia;
        this.mota = mota;
        this.details = details != null ? details : new ArrayList<>();
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

    public List<PriceSlot> getDetails() {
        return details;
    }
}

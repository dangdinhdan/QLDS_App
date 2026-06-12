package com.example.mobile.model;

public class Court {
    private final int id;
    private String courtCode;
    private String name;
    private CourtStatus status;
    private double hourlyRate;
    private String surfaceType;
    private String imageUrl;
    private String estimatedCompletionDate;
    private Integer idBanggia;

    public Court(int id, String courtCode, String name, CourtStatus status, double hourlyRate, String surfaceType, String imageUrl, String estimatedCompletionDate) {
        this.id = id;
        this.courtCode = courtCode;
        this.name = name;
        this.status = status;
        this.hourlyRate = hourlyRate;
        this.surfaceType = surfaceType;
        this.imageUrl = imageUrl;
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public Court(int id, String name, CourtStatus status, double hourlyRate, String surfaceType, String imageUrl, String estimatedCompletionDate) {
        this(id, "PB-" + String.format("%02d", id), name, status, hourlyRate, surfaceType, imageUrl, estimatedCompletionDate);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourtStatus getStatus() {
        return status;
    }

    public void setStatus(CourtStatus status) {
        this.status = status;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public void setEstimatedCompletionDate(String estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public Integer getIdBanggia() {
        return idBanggia;
    }

    public void setIdBanggia(Integer idBanggia) {
        this.idBanggia = idBanggia;
    }
}


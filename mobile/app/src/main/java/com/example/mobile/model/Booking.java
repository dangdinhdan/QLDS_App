package com.example.mobile.model;

public class Booking {
    private final int id;
    private final int courtId;
    private final String playerName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private double fee;

    public Booking(int id, int courtId, String playerName, String date, String startTime, String endTime, double fee) {
        this.id = id;
        this.courtId = courtId;
        this.playerName = playerName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
    }

    public int getId() {
        return id;
    }

    public int getCourtId() {
        return courtId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double calculateDurationHours() {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            double startHour = Integer.parseInt(startParts[0]) + Integer.parseInt(startParts[1]) / 60.0;
            double endHour = Integer.parseInt(endParts[0]) + Integer.parseInt(endParts[1]) / 60.0;
            double duration = endHour - startHour;
            return duration > 0 ? duration : 1.0;
        } catch (Exception e) {
            return 1.0;
        }
    }
}

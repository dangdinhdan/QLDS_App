package com.example.backend.dto;

public class BookingDTO {
    private Integer id;
    private Integer courtId;
    private String playerName;
    private String phoneNumber;
    private String date;
    private String startTime;
    private String endTime;
    private Double fee;
    private String status;
    private String notes;

    public BookingDTO() {
    }

    public BookingDTO(Integer id, Integer courtId, String playerName, String phoneNumber, String date, String startTime, String endTime, Double fee, String status, String notes) {
        this.id = id;
        this.courtId = courtId;
        this.playerName = playerName;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
        this.status = status;
        this.notes = notes;
    }

    public static BookingDTOBuilder builder() {
        return new BookingDTOBuilder();
    }

    public static class BookingDTOBuilder {
        private Integer id;
        private Integer courtId;
        private String playerName;
        private String phoneNumber;
        private String date;
        private String startTime;
        private String endTime;
        private Double fee;
        private String status;
        private String notes;

        public BookingDTOBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public BookingDTOBuilder courtId(Integer courtId) {
            this.courtId = courtId;
            return this;
        }

        public BookingDTOBuilder playerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public BookingDTOBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public BookingDTOBuilder date(String date) {
            this.date = date;
            return this;
        }

        public BookingDTOBuilder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public BookingDTOBuilder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public BookingDTOBuilder fee(Double fee) {
            this.fee = fee;
            return this;
        }

        public BookingDTOBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BookingDTOBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public BookingDTO build() {
            return new BookingDTO(id, courtId, playerName, phoneNumber, date, startTime, endTime, fee, status, notes);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

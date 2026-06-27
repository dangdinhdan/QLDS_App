package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}

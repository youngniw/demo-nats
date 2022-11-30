package com.example.testnats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class OperationLogDto {
    private long logId;
    private int serialNumber;
    private LocalDateTime logTime;
    private double longitude;
    private double latitude;
    private int gear;
}

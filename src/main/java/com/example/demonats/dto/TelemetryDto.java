package com.example.demonats.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class TelemetryDto {
    @NotNull
    private int serialNumber;

    @NotNull
    private double longitude;

    @NotNull
    private double latitude;

    @NotNull
    private int gear;
}

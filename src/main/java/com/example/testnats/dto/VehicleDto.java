package com.example.testnats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Getter
public class VehicleDto {
    private long vehicleId;
    private int serialNumber;
}

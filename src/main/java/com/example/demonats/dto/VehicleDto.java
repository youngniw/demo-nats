package com.example.demonats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class VehicleDto {
    private long vehicleId;
    private int serialNumber;
}

package com.example.demonats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CarDto {
    private long carId;
    private int serialNumber;
}

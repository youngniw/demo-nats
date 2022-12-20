package com.example.demonats.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CarListPageDto {
    private final int totalPages;
    private final long totalElements;
    private final int numberOfElements;
    private final List<CarDto> cars;

    @Builder
    public CarListPageDto(int totalPages, long totalElements, int numberOfElements, List<CarDto> cars) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.numberOfElements = numberOfElements;
        this.cars = cars;
    }
}

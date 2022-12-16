package com.example.demonats.service;

import com.example.demonats.dto.OperationLogDto;
import com.example.demonats.dto.TelemetryDto;
import com.example.demonats.dto.CarDto;
import com.example.demonats.entity.OperationLog;
import com.example.demonats.entity.Car;
import com.example.demonats.repository.OperationLogRepository;
import com.example.demonats.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final OperationLogRepository operationLogRepository;

    // 차량 기본 정보 조회
    @Transactional(readOnly = true)
    public CarDto getCarInfo(int serialNumber) {
        Car carBySerialNum = carRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new RuntimeException("차량이 존재하지 않습니다."));

        return CarDto.builder()
                .carId(carBySerialNum.getCarId())
                .serialNumber(carBySerialNum.getSerialNumber())
                .build();
    }

    // 차량 목록 기본 정보 조회
    @Transactional(readOnly = true)
    public List<CarDto> getCarListInfo() {
        return carRepository.findAll().stream()
                .map(car -> CarDto.builder()
                        .carId(car.getCarId())
                        .serialNumber(car.getSerialNumber())
                        .build())
                .collect(Collectors.toList());
    }

    public List<OperationLogDto> getOperationLogList(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = LocalDateTime.of(startDate, LocalTime.of(0, 0));
        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));
        List<OperationLog> operationLogs = operationLogRepository.findAllByLogTimeBetweenOrderByLogTime(startTime, endTime);

        return operationLogs.stream()
                .map(operationLog -> OperationLogDto.builder()
                        .logId(operationLog.getLogId())
                        .serialNumber(operationLog.getCar().getSerialNumber())
                        .logTime(operationLog.getLogTime())
                        .longitude(operationLog.getLongitude())
                        .latitude(operationLog.getLatitude())
                        .gear(operationLog.getGear())
                        .build())
                .collect(Collectors.toList());
    }

    // 평가보드로부터 받은 차량 정보 저장
    @Transactional
    public Car saveOrFindCar(int serialNumber) {
        Optional<Car> carBySerialNum = carRepository.findBySerialNumber(serialNumber);

        if (carBySerialNum.isEmpty()) {
            Car car = Car.builder()
                    .serialNumber(serialNumber)
                    .build();

            return carRepository.save(car);
        }
        return carBySerialNum.get();
    }

    // 평가보드로부터 받은 차량 정보 저장
    @Transactional
    public void saveCarOperationLog(TelemetryDto telemetryDto, Car car) {
        // Car 저장 후, OperationLog 저장
        OperationLog log = OperationLog.builder()
                .car(car)
                .longitude(telemetryDto.getLongitude())
                .latitude(telemetryDto.getLatitude())
                .gear(telemetryDto.getGear())
                .build();

        operationLogRepository.save(log);
    }
}

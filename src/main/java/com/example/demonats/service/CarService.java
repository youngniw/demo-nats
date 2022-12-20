package com.example.demonats.service;

import com.example.demonats.dto.*;
import com.example.demonats.entity.OperationLog;
import com.example.demonats.entity.Car;
import com.example.demonats.repository.OperationLogRepository;
import com.example.demonats.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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

    // 차량 최신 상태 정보 조회
    @Transactional(readOnly = true)
    public CarCurrentStateDto getCarCurrentStateInfo(int serialNumber) {
        String stateTime = "운행 정보 없음";

        OperationLog currentOperationLog = operationLogRepository.findDistinctFirstByCar_SerialNumberOrderByLogTimeDesc(serialNumber)
                .orElseThrow(() -> new RuntimeException("차량 정보가 존재하지 않습니다."));

        if (currentOperationLog.getGear() == 1) {
            // 바로 직전의 운행 상태 조회 (반환: 직전의 운행 상태 시간~현재 시간 차이)
            Optional<OperationLog> operationLogRunning = operationLogRepository.findFirstByCar_SerialNumberAndGearIsNotOrderByLogTimeDesc(serialNumber, 1);

            if (operationLogRunning.isPresent())
                stateTime = getTimeDifference(operationLogRunning.get().getLogTime(), LocalDateTime.now());
        }
        else {  // 바로 직전의 운행 정지 상태 조회 (반환: 직전의 정지 상태 시간~현재 시간 차이)
            OperationLog operationLogWaiting = operationLogRepository.findFirstByCar_SerialNumberAndGearIsOrderByLogTimeDesc(serialNumber, 1)
                    .orElseGet(() -> operationLogRepository.findFirstByCar_SerialNumberOrderByLogTime(serialNumber)
                            .orElseThrow(() -> new RuntimeException("서버 내부 오류입니다.")));

            stateTime = getTimeDifference(operationLogWaiting.getLogTime(), LocalDateTime.now());
        }
        return CarCurrentStateDto.builder()
                .serialNumber(serialNumber)
                .gear(currentOperationLog.getGear())
                .stateTime(stateTime)
                .build();
    }

    private String getTimeDifference(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        if (duration.toHours() == 0L)
            return String.format("%d분", duration.toMinutes());
        else
            return String.format("%d시간", duration.toHours());
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

    // 차량 목록 기본 정보 조회
    @Transactional(readOnly = true)
    public CarListPageDto getCarListPageInfo(int page, Integer serialNumber) {
        PageRequest pageRequest = PageRequest.of(page-1, 8, Sort.by("carId").ascending());  // page 는 0부터 시작

        Page<Car> cars;
        if (serialNumber == null)
            cars = carRepository.findAll(pageRequest);
        else
            cars = carRepository.findBySerialNumber(serialNumber, pageRequest);

        return CarListPageDto.builder()
                .totalPages(cars.getTotalPages())
                .totalElements(cars.getTotalElements())
                .numberOfElements(cars.getNumberOfElements())
                .cars(cars.getContent().stream()
                        .map(car -> CarDto.builder()
                                .carId(car.getCarId())
                                .serialNumber(car.getSerialNumber())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
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

package com.example.demonats.service;

import com.example.demonats.dto.OperationLogDto;
import com.example.demonats.dto.TelemetryDto;
import com.example.demonats.dto.VehicleDto;
import com.example.demonats.entity.OperationLog;
import com.example.demonats.entity.Vehicle;
import com.example.demonats.repository.OperationLogRepository;
import com.example.demonats.repository.VehicleRepository;
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
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final OperationLogRepository operationLogRepository;

    // 차량 기본 정보 조회
    @Transactional(readOnly = true)
    public VehicleDto getVehicleInfo(int serialNumber) {
        Vehicle vehicleBySerialNum = vehicleRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new RuntimeException("차량이 존재하지 않습니다."));

        return VehicleDto.builder()
                .vehicleId(vehicleBySerialNum.getVehicleId())
                .serialNumber(vehicleBySerialNum.getSerialNumber())
                .build();
    }

    // 차량 목록 기본 정보 조회
    @Transactional(readOnly = true)
    public List<VehicleDto> getVehicleListInfo() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> VehicleDto.builder()
                        .vehicleId(vehicle.getVehicleId())
                        .serialNumber(vehicle.getSerialNumber())
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
                        .serialNumber(operationLog.getVehicle().getSerialNumber())
                        .logTime(operationLog.getLogTime())
                        .longitude(operationLog.getLongitude())
                        .latitude(operationLog.getLatitude())
                        .gear(operationLog.getGear())
                        .build())
                .collect(Collectors.toList());
    }

    // 평가보드로부터 받은 차량 정보 저장
    @Transactional
    public Vehicle saveOrFindVehicle(int serialNumber) {
        Optional<Vehicle> vehicleBySerialNum = vehicleRepository.findBySerialNumber(serialNumber);

        if (vehicleBySerialNum.isEmpty()) {
            Vehicle vehicle = Vehicle.builder()
                    .serialNumber(serialNumber)
                    .build();

            return vehicleRepository.save(vehicle);
        }
        return vehicleBySerialNum.get();
    }

    // 평가보드로부터 받은 차량 정보 저장
    @Transactional
    public void saveVehicleOperationLog(TelemetryDto telemetryDto, Vehicle vehicle) {
        // Vehicle 저장 후, OperationLog 저장
        OperationLog log = OperationLog.builder()
                .vehicle(vehicle)
                .longitude(telemetryDto.getLongitude())
                .latitude(telemetryDto.getLatitude())
                .gear(telemetryDto.getGear())
                .build();

        operationLogRepository.save(log);
    }
}

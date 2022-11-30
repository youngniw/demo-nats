package com.example.demonats.controller;

import com.example.demonats.dto.OperationLogDto;
import com.example.demonats.dto.TelemetryDto;
import com.example.demonats.dto.VehicleDto;
import com.example.demonats.entity.Vehicle;
import com.example.demonats.nats.NatsComponent;
import com.example.demonats.service.VehicleService;
import com.example.demonats.websocket.handler.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    private final VehicleService vehicleService;
    private final NatsComponent natsComponent;
    private final ObjectMapper objectMapper;
    private final WebSocketHandler webSocketHandler;

    // 차량 정보 저장
    @PostMapping("/telemetry")
    public ResponseEntity<String> setMonitoringVehicleData(@RequestBody @Valid TelemetryDto telemetry) throws Exception {
        // 소켓 전달
        Set<WebSocketSession> sessions = webSocketHandler.getSessions();
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(telemetry)));
        }

        // nats 서버로 전달
        ObjectMapper mapper = new ObjectMapper();
        natsComponent.publish("msg.vehicle.data", mapper.writeValueAsString(telemetry));
        natsComponent.publish("msg.vehicle.data." + telemetry.getSerialNumber(), mapper.writeValueAsString(telemetry));

        Vehicle vehicle = vehicleService.saveOrFindVehicle(telemetry.getSerialNumber());
        vehicleService.saveVehicleOperationLog(telemetry, vehicle);          // 차량 정보 + 차량 운행 정보 저장

        return ResponseEntity.ok("success");
    }

    // 차량 정보 조회
    @GetMapping("/{serialNumber}")
    public ResponseEntity<VehicleDto> getVehicleInfo(@PathVariable("serialNumber") int serialNumber) {
        VehicleDto vehicleInfo = vehicleService.getVehicleInfo(serialNumber);

        return ResponseEntity.ok(vehicleInfo);
    }

    // 차량 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<VehicleDto>> getVehicleListInfo() {
        List<VehicleDto> vehicleInfoList = vehicleService.getVehicleListInfo();

        return ResponseEntity.ok(vehicleInfoList);
    }

    // 차량 운행 정보 조회
    @GetMapping("/operation-log")
    public ResponseEntity<List<OperationLogDto>> getVehicleOperationHistory(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<OperationLogDto> vehicleInfoList = vehicleService.getOperationLogList(startDate, endDate);

        return ResponseEntity.ok(vehicleInfoList);
    }
}

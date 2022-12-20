package com.example.demonats.controller;

import com.example.demonats.dto.CarListPageDto;
import com.example.demonats.dto.OperationLogDto;
import com.example.demonats.dto.TelemetryDto;
import com.example.demonats.dto.CarDto;
import com.example.demonats.entity.Car;
import com.example.demonats.nats.NatsComponent;
import com.example.demonats.service.CarService;
import com.example.demonats.websocket.handler.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/car")
public class CarController {
    private final CarService carService;
    private final NatsComponent natsComponent;
    private final ObjectMapper objectMapper;
    private final WebSocketHandler webSocketHandler;

    // 차량 정보 저장
    @PostMapping("/telemetry")
    public ResponseEntity<String> setMonitoringCarData(@RequestBody @Valid TelemetryDto telemetry) throws Exception {
        // 소켓 전달
        Set<WebSocketSession> sessions = webSocketHandler.getSessions();
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(telemetry)));
        }

        // nats 서버로 전달
        ObjectMapper mapper = new ObjectMapper();
        natsComponent.publish("msg.car.data", mapper.writeValueAsString(telemetry));
        natsComponent.publish("msg.car.data." + telemetry.getSerialNumber(), mapper.writeValueAsString(telemetry));

        Car car = carService.saveOrFindCar(telemetry.getSerialNumber());
        carService.saveCarOperationLog(telemetry, car);          // 차량 정보 + 차량 운행 정보 저장

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{carId}")
                .buildAndExpand(car.getCarId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    // 차량 정보 조회
    @GetMapping("/{serialNumber}")
    public ResponseEntity<CarDto> getCarInfo(@PathVariable("serialNumber") int serialNumber) {
        CarDto carInfo = carService.getCarInfo(serialNumber);

        return ResponseEntity.ok(carInfo);
    }

    // 차량 목록 조회 (페이지)
    @GetMapping("/list")
    public ResponseEntity<CarListPageDto> getCarListPageInfo(@RequestParam("page") int page,
                                                             @RequestParam(value = "serialNumber", required = false) Integer serialNumber) {
        CarListPageDto carInfoList = carService.getCarListPageInfo(page, serialNumber);

        return ResponseEntity.ok(carInfoList);
    }

    // 차량 운행 정보 조회
    @GetMapping("/operation-log")
    public ResponseEntity<List<OperationLogDto>> getCarOperationHistory(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<OperationLogDto> carInfoList = carService.getOperationLogList(startDate, endDate);

        return ResponseEntity.ok(carInfoList);
    }
}

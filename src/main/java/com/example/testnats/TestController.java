package com.example.testnats;

import com.example.testnats.nats.NatsComponent;
import com.example.testnats.websocket.handler.WebSocketHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final NatsComponent natsComponent;
    private final WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    // 응답 없는 publish
    @GetMapping("/test/publish")
    public void testPublish() {
        natsComponent.publish("msg.car", "Publish by car");
    }

    // 응답 subject 포함하는 publish
    @GetMapping("/test/publish/reply")
    public void testPublishAndReply() {
        natsComponent.publish("msg.car", "msg.data", "Publish by car Reply to data");
    }

    // 요청
    @GetMapping("/test/request")
    public ResponseEntity<String> testRequest() throws ExecutionException, InterruptedException, TimeoutException {
        String response = natsComponent.request("msg.data", "Request to Data");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 예시 (서버에서 클라이언트로 웹소켓을 통해 데이터 전달)
//    @GetMapping("/test")
//    public ResponseEntity<String> test() throws Exception {
//        Set<WebSocketSession> sessions = webSocketHandler.getSessions();
//        for (WebSocketSession session : sessions) {
//            VehicleVO vehicleVO = VehicleVO.builder()
//                    .name("도시개발공사 1호차")
//                    .gear("D")
//                    .build();
//
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(vehicleVO)));
//        }
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @Builder
//    public static class VehicleVO {
//        @JsonProperty
//        private String name;
//
//        @JsonProperty
//        private String gear;
//    }

    @PostMapping("/item")
    public ResponseEntity<String> setMonitoringCarData(@RequestBody VehicleDTO vehicleData) throws Exception {
        // text/json으로 전달 시에는 @RequestBody 사용 가능
        // @RequestBody Map<String, Object> data
        log.info("vehicleData = " + vehicleData);

        Set<WebSocketSession> sessions = webSocketHandler.getSessions();
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(vehicleData)));
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("/telemetry")
    public ResponseEntity<String> setMonitoringVehicleData(@RequestBody String data) throws JsonProcessingException {
        natsComponent.publish("msg.vehicle.data", data);

//        ObjectMapper mapper = new ObjectMapper();
//        VehicleDataDto vehicleData = mapper.readValue(data, VehicleDataDto.class);
//        vehicleService.saveVehicle(vehicleData);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}

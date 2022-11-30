package com.example.testnats.nats.handler;

import com.example.testnats.service.VehicleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("async")
@RequiredArgsConstructor
@Component
public class NatsAsyncMessageHandler {
    private final Connection natsConnection;
    private final VehicleService vehicleService;

//    @Value("${nats.stream.subject}")
//    private String subject;

    @Value("${nats.stream.queue}")
    private String queue;
    private Dispatcher dispatcher;

    @PostConstruct
    private void init() {
        log.info("Dispatcher can manage subject...");

        dispatcher = natsConnection.createDispatcher((msg) -> {});

        // 구독 예시
        Subscription subToVehicle = dispatcher.subscribe("msg.vehicle.telemetry", (message) -> {
            String data = new String(message.getData(), StandardCharsets.UTF_8);
            log.info(String.format("Received Message from %s: %s", message.getSubject(), data));

            /* 데이터 가공해서 처리 */

            if (message.getReplyTo() != null) {
                natsConnection.publish(message.getReplyTo(), "OK".getBytes());      // 요청에 대한 응답 반환
            }
            else ;      // 다시 응답 반환
        });

        // 차량 관련 요청 통로
        Subscription subToVehicleRequest = dispatcher.subscribe("msg.vehicle.request.*", (message) -> {
            if (message.getReplyTo() != null) {     // 차량 요청이 있을 시 (ex. msg.vehicle.request.1234로 "info"라는 데이터를 담아 요청 받을 시, 차량 정보 반환)
                int serialNumber = Integer.parseInt(message.getSubject().split("\\.")[3]);

                String data = new String(message.getData(), StandardCharsets.UTF_8);    // 요청 주제
                if (data.equals("info")) {          // 차량 정보 조회
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        natsConnection.publish(message.getReplyTo(), mapper.writeValueAsBytes(vehicleService.getVehicleInfo(serialNumber)));
                    } catch (JsonProcessingException e) {
                        natsConnection.publish(message.getReplyTo(), "{}".getBytes());
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @PreDestroy
    private void destroy() {
        dispatcher.unsubscribe("msg.vehicle.telemetry");
        dispatcher.unsubscribe("msg.vehicle.request.*");
    }
}

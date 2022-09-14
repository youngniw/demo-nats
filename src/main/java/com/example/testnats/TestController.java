package com.example.testnats;

import com.example.testnats.nats.NatsComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final NatsComponent natsComponent;

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
}

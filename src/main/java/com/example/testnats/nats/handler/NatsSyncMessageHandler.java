package com.example.testnats.nats.handler;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("sync")
@Component
public class NatsSyncMessageHandler {
    private final Connection natsConnection;
    private Subscription subscription;

    @Value("${nats.stream.subject}")
    private String subject;
    @Value("${nats.stream.queue}")
    private String queue;
    private MessageHandlerThread thread;

    @Autowired
    public NatsSyncMessageHandler(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    @PostConstruct
    private void init() {
        if (StringUtils.hasText(queue)) {
            subscription = natsConnection.subscribe(subject, queue);
        }
        else {
            subscription = natsConnection.subscribe(subject);
        }
        this.thread = new MessageHandlerThread();
        thread.start();
    }

    @PreDestroy
    private void destroy() {
        subscription.unsubscribe();
        thread.interrupt();
    }

    // Request에 대한 Response(응답) -> subscribe 내용
    public class MessageHandlerThread extends Thread {
        public void run() {
            System.out.println(this.getName() + ": Message Thread is running...");
            while (true) {
                try {
                    Message message = subscription.nextMessage(1000);
                    if (message != null) {
                        String data = new String(message.getData(), StandardCharsets.UTF_8);
                        log.info(String.format("Received Message from %s: %s", message.getSubject(), data));

                        /* 데이터 가공해서 처리 */

                        if (message.getReplyTo() != null)
                            natsConnection.publish(message.getReplyTo(), "OK".getBytes());      // 요청에 대한 응답 반환
                        else
                            ;      // 다시 응답 반환
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

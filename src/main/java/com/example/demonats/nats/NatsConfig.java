package com.example.demonats.nats;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class NatsConfig {
    @Value("${nats.stream.uri}")
    private String uri;

    // Nats 서버와 연결
    @Bean
    Connection initConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(uri)
                .userInfo("admin", "admin_")
                .maxReconnects(10)
                .reconnectWait(Duration.ofSeconds(5))
                .connectionTimeout(Duration.ofSeconds(5))
                .connectionListener((conn, type) -> {
                    if (type == ConnectionListener.Events.CONNECTED) {
                        log.info("Connected to Nats Server");
                    } else if (type == ConnectionListener.Events.RECONNECTED) {
                        log.info("Reconnected to Nats Server");
                    } else if (type == ConnectionListener.Events.DISCONNECTED) {
                        log.error("Disconnected to Nats Server, reconnect attempt in seconds");
                    } else if (type == ConnectionListener.Events.CLOSED) {
                        log.info("Closed connection with Nats Server");
                    } else {
                        log.info("Connection Type: {}", type.toString());
                    }
                })
                .build();

        return Nats.connect(options);
    }
}

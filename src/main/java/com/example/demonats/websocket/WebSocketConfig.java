package com.example.demonats.websocket;

import com.example.demonats.websocket.handler.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;

    @Autowired
    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // path: "ws://서버주소:8080/ws/data"
        registry.addHandler(webSocketHandler, "/ws/data")
                .setAllowedOrigins("*")
                .withSockJS();
        registry.addHandler(webSocketHandler, "/ws/data")
                .setAllowedOrigins("*");
    }
}

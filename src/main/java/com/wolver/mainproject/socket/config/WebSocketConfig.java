package com.wolver.mainproject.socket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 보내는 url
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("queue/chat");
        config.setApplicationDestinationPrefixes("/app");
    }

    // 소켓 연결 url
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wss/websocket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
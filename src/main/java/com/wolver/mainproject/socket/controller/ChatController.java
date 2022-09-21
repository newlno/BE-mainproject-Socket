package com.wolver.mainproject.socket.controller;

import com.wolver.mainproject.socket.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {


    private final SimpMessagingTemplate messagingTemplate; //특정 브로커로 메시지 전달

    // pub/chat/enter
    @MessageMapping("/chat/enter")
    public void enter(@Payload ChatMessage message) {
        message.setMessageType(message.getMessageType());
        message.setRoomId(message.getRoomId());
        message.setSessionId(message.getSessionId());
        message.setMessage(message.getMessage());

        log.info("타입" + message.getMessageType());
        log.info("룸" + message.getRoomId());
        log.info("세션" + message.getSessionId());
        log.info("메시지" + message.getMessage());
        messagingTemplate.convertAndSend("/sub/chat/enter/" + message.getRoomId(), message);
    }
}



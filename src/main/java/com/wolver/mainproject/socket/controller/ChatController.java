package com.wolver.mainproject.socket.controller;

import com.wolver.mainproject.socket.domain.ChatMessage;
import com.wolver.mainproject.socket.domain.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.Queue;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate; //특정 브로커로 메시지 전달
    private final SimpMessageSendingOperations messageSending;

    // pub/chat/enter
    @MessageMapping("/chat/enter/{roomId}")
    public void enter(@DestinationVariable String roomId, @Payload ChatMessage message) {
        message.setMessageType(message.getMessageType());
        message.setRoomId(roomId);
        message.setSessionId(message.getSessionId());
        message.setMessage(message.getMessage());

        log.info("타입" + message.getMessageType());
        log.info("룸" + roomId);
        log.info("세션" + message.getSessionId());
        log.info("메시지" + message.getMessage());
        messagingTemplate.convertAndSend("/sub/chat/enter/" + roomId, message);
    }

    public void disconnectedSend(String roomId, String sessionId) {

        ChatMessage message = new ChatMessage();
        message.setMessageType(MessageType.DISCONNECTED);
        message.setRoomId(roomId);
        message.setSessionId(sessionId);
        message.setMessage(sessionId + " 해당 유저가 나갔습니다.");

        log.info("나간 룸id 메시지 " + roomId);
        log.info("나간 세션id 메시지 " + sessionId);
        messageSending.convertAndSend("/sub/chat/enter/" + roomId, message);

    }

}



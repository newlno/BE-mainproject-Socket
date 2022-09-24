package com.wolver.mainproject.socket.controller;

import com.wolver.mainproject.socket.domain.ChatMessage;
import com.wolver.mainproject.socket.domain.MessageType;
import com.wolver.mainproject.socket.service.ChatService;
import com.wolver.mainproject.socket.util.WebSocketEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService service;
    private final WebSocketEvent event;


    // pub/chat/enter
    @MessageMapping("/chat/enter/{roomId}")
    public void enter(@DestinationVariable String roomId, @Payload ChatMessage message) throws IOException {
        if (event.findSessionId(roomId) == 2) {
            message.setMessageType(message.getMessageType());
            message.setRoomId(roomId);
            message.setSessionId(message.getSessionId());
            message.setMessage(message.getMessage());

            log.info("타입" + message.getMessageType());
            log.info("룸" + roomId);
            log.info("세션" + message.getSessionId());
            log.info("메시지" + message.getMessage());
            service.sendMessage(roomId, message);
        } else {
            ChatMessage serverMessage = new ChatMessage();
            serverMessage.setMessageType(MessageType.DISCONNECTED);
            serverMessage.setMessage("" + message.getSessionId() + " 님이 퇴장했습니다.");
            serverMessage.setRoomId(roomId);
            serverMessage.setSessionId(message.getSessionId());

            log.info("나갔을때 타입" + serverMessage.getMessageType());
            log.info("나갔을때 룸" + roomId);
            log.info("나간사람 " + message.getSessionId());
            log.info("나갔을때 메시지 " + serverMessage.getMessage());
            service.sendMessage(roomId, serverMessage);
        }
    }

}



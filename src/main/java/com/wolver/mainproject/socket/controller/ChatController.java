package com.wolver.mainproject.socket.controller;

import com.wolver.mainproject.socket.model.ChatMessage;
import com.wolver.mainproject.socket.model.MessageType;
import com.wolver.mainproject.socket.service.ChatService;
import com.wolver.mainproject.socket.util.WebSocketEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ChatController {


    private final ChatService chatService;
    private final WebSocketEvent event;


    // 케이스로 이프문 조져버리기
    // pub/chat/enter
    @MessageMapping("/chat/enter/{roomId}")
    public void enter(@DestinationVariable String roomId, @Payload ChatMessage message) throws IOException {
        if (message.getMessageType().equals(MessageType.FINISH)) {
            event.finish(message);
            chatService.gameFinish(roomId, message);
        } else if (message.getMessageType().equals(MessageType.DISCONNECTED)) {
            chatService.gameOut(roomId, message);
        } else {
            if (event.findCookieSize(roomId) == 2) {
                chatService.gameChat(roomId, message);
            } else {
                chatService.aloneUser(roomId, message);
            }
        }
    }


}



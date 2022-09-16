package com.wolver.mainproject.socket.controller;

import com.wolver.mainproject.socket.domain.ChatMessage;
import com.wolver.mainproject.socket.domain.MessageType;
import com.wolver.mainproject.socket.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class ChatController {

    private ChatService chatService;


    @MessageMapping("/sendMessage/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") String chatRoomId,
                            @Payload ChatMessage chatMessage) {
        if (!StringUtils.hasText(chatRoomId) || chatMessage == null) {
            return;
        }
        if (chatMessage.getMessageType() == MessageType.CHAT) {
            chatService.sendMessage(chatRoomId, chatMessage);
        }
    }
}

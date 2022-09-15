package com.wolver.mainproject.socket.service;

import com.wolver.mainproject.socket.domain.ChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private SimpMessagingTemplate messagingTemplate;


    // 메시지 전송
    public void sendMessage(String chatRoomId, ChatMessage chatMessage) {
        String destination = getDestination(chatRoomId);
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

    // 메시지 전송을 위한 url 만들기
    private String getDestination(String chatRoomId) {
        return "/queue/chat/" + chatRoomId;
    }
}
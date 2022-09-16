package com.wolver.mainproject.socket.util;

import com.wolver.mainproject.socket.domain.ChatMessage;
import com.wolver.mainproject.socket.domain.MessageType;
import com.wolver.mainproject.socket.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEvent {

    private ChatService chatService;
//    private ArrayList<String> waitingQueue; //랜덤매칭
    private Queue<String> waitingQueue; //선착순매칭
    private HashMap<String, String> connectedQueue;
    // {key : websocket session id, value : chat room id}

    // 연결 성공
    @EventListener
    public void WebSocketConnectedEvent(SessionConnectedEvent event) {
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        String sessionId = (String) generic.getHeaders().get("simpSessionId");
        waitingQueue.add(sessionId);
        if (waitingQueue.size() > 2) {
            String chatRoomId = UUID.randomUUID().toString();
            // 랜덤매칭
//            int user1 = ((int) (Math.random() * waitingQueue.size()) + 1);
//            int user2 = ((int) (Math.random() * waitingQueue.size()) + 1);
//            String user1SessionId = waitingQueue.get(user1);
//            String user2SessionId = waitingQueue.get(user2);
//            connectedQueue.put(user1SessionId, chatRoomId);
//            connectedQueue.put(user2SessionId, chatRoomId);
            // 선착순매칭
            connectedQueue.put(waitingQueue.poll(),chatRoomId);
            connectedQueue.put(waitingQueue.poll(),chatRoomId);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageType(MessageType.JOIN);
            chatService.sendMessage(chatRoomId, chatMessage);
        }
    }

    // 연결 종료
    @EventListener
    public void WebSocketDisconnectedEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String chatroomId = connectedQueue.get(sessionId);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType(MessageType.DISCONNECTED);
        chatService.sendMessage(chatroomId, chatMessage);
    }
}

package com.wolver.mainproject.socket.service;

import com.wolver.mainproject.socket.domain.ChatMessage;
import com.wolver.mainproject.socket.domain.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messageSending;
    //    private final ArrayList<String> waitingQueue = new ArrayList<>(); //랜덤매칭 대기열
    private final Queue<String> waitingQueue = new LinkedList<>(); //선착순매칭 대기열
    private final HashMap<String, String> connectedQueue = new HashMap<>(); //연결된 유저
    // {key : websocket session id, value : chat room id}

    public void matchingRoom(String sessionId) {
        if (connectedQueue.get(sessionId) == null) {
            log.info(sessionId);
            waitingQueue.add(sessionId);
            if (waitingQueue.size() > 1) {
                // 무한루프 없이 프론트에서 메시지 대기를하다가 도착하면 세션확인하고 맞으면 그때 재구독
                log.info("대기인원 확인 " + waitingQueue);
                log.info("대기인원 2명이상 if문진입 " + sessionId);

                // 방 생성
                String chatRoomId = UUID.randomUUID().toString();

                String user1SessionId = waitingQueue.poll();
                log.info("첫번째 유저정보 " + user1SessionId);
                log.info("처음 꺼내고 남은 대기인원 " + waitingQueue);
                connectedQueue.put(user1SessionId, chatRoomId);

                String user2SessionId = waitingQueue.poll();
                log.info("두번째 유저정보" + user2SessionId);
                log.info("두번째 꺼내고 남은 대기인원 " + waitingQueue);
                connectedQueue.put(user2SessionId, chatRoomId);

                log.info("매칭된 인원 확인 " + connectedQueue);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessageType(MessageType.JOIN);
                chatMessage.setRoomId(chatRoomId);
                chatMessage.setSessionId(sessionId); // 리스트로 2명 보냄
                messageSending.convertAndSend("/sub/chat/join", chatMessage);
                log.info("클라이언트에게 메시지 보내기");
                log.info(String.valueOf(chatMessage));

                chatMessage.setMessageType(MessageType.JOIN);
                chatMessage.setRoomId(chatRoomId);
                chatMessage.setSessionId(sessionId); // 리스트로 2명 보냄
                messageSending.convertAndSend("/sub/chat/join", chatMessage);
                log.info("클라이언트에게 메시지 보내기");
                log.info(String.valueOf(chatMessage));
            }
        }
    }

    public void sendMessage(String roomId,ChatMessage message) {
        messageSending.convertAndSend("/sub/chat/enter/" + roomId, message);
    }

}

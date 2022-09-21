package com.wolver.mainproject.socket.util;

import com.wolver.mainproject.socket.domain.ChatMessage;
import com.wolver.mainproject.socket.domain.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import javax.servlet.http.HttpSession;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEvent {

    private final SimpMessageSendingOperations messageSending;
    //    private final ArrayList<String> waitingQueue = new ArrayList<>(); //랜덤매칭 대기열
    private final Queue<String> waitingQueue = new LinkedList<>(); //선착순매칭 대기열
    private final HashMap<String, String> connectedQueue = new HashMap<>(); //연결된 유저
    // {key : websocket session id, value : chat room id}
    private final HttpSession httpSession;

    int waitingTime = 0;


    @EventListener // 구독이벤트 진행시
    public void WebSocketSubscribeEvent(SessionSubscribeEvent event) throws InterruptedException {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("접속유저 최초헤더확인 " + accessor);
        String sessionId = httpSession.getId();
        log.info(sessionId);
        waitingQueue.add(sessionId);
        if (waitingQueue.contains(sessionId)) {
            log.info("큐에 담기고 if문진입 " + sessionId);
            while (waitingTime > 120) {
                // 스레드 돌리고 모니터객체 만들어서 비동기 <- 찾아보기
                log.info("무한루프 진입 " + sessionId);
                Thread.sleep(1000);
                waitingTime++;
                if (waitingQueue.size() > 1) {
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
                    chatMessage.setSessionId(httpSession.getId());
                    messageSending.convertAndSend("/sub/chat/join", chatMessage);
                    log.info("클라이언트에게 메시지 보내기");
                    log.info(String.valueOf(chatMessage));
                }
            }
        }
    }


    @EventListener // 웹소켓 연결시
    public void WebSocketConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
        log.info("소켓 연결된 유저정보 " + sessionId);
    }

    // 연결 종료
    @EventListener
    public void WebSocketDisconnectedEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
        log.info("접속종료한 유저정보 " + sessionId);
        String disconnectRoomId = connectedQueue.get(sessionId); // 키를 통한 룸Id 확인
        log.info("접속종료한 유저의 RoomId " + disconnectRoomId);
        connectedQueue.remove(sessionId);

        String connectedUser = findSessionId(disconnectRoomId);
        connectedQueue.remove(connectedUser);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType(MessageType.DISCONNECTED);
        chatMessage.setRoomId(disconnectRoomId);
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessage("해당 방에 남은 유저 :" + connectedUser);
        messageSending.convertAndSend("/sub/chat/enter" + disconnectRoomId, chatMessage);
        log.info("접속종료 메시지 " + chatMessage);
    }

    public String findSessionId(String roomId) {
        for (String key : connectedQueue.keySet()) {
            if (connectedQueue.get(key).equals(roomId))
                return connectedQueue.get(key);
            break;
        }
        return "해당유저가 없습니다.";
    }
}

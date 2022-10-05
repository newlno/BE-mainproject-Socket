package com.wolver.mainproject.socket.util;

import com.wolver.mainproject.socket.model.ChatMessage;
import com.wolver.mainproject.socket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WebSocketEvent {

    private final ChatService chatService;

    private static final Queue<String> waitingQueue = new LinkedList<>(); //매칭 대기열 세션
    private static final Queue<String> waitingNickname = new LinkedList<>(); //매칭 대기열 닉네임
    private static final Queue<String> waitingCookie = new LinkedList<>(); //매칭 대기열 쿠키
    private static final Queue<String> waitingUserImg = new LinkedList<>(); //매칭 대기열 프로필이미지
    private static final HashMap<String, String> waitingUser = new HashMap<>(); //매칭 대기 유저
    // {key : websocket session id, value : cookie}
    private static final HashMap<String, String> connectedUser = new HashMap<>(); //매칭 연결된 유저
    // {key : websocket session id, value : chat room id}
    private static final HashMap<String, String> gameUser = new HashMap<>(); //게임중인 유저
    // {key : websocket session id, value : chat room id}

    @EventListener // 구독이벤트
    public void WebSocketSubscribeEvent(SessionSubscribeEvent event) throws IOException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
        String nickname = (accessor.getNativeHeader("nickname").get(0));
        String profileImage = (accessor.getNativeHeader("profileImage").get(0));
        String cookie = (accessor.getNativeHeader("cookie").get(0));

        if (waitingCookie.stream().anyMatch(waitingCookie -> waitingCookie.equals(cookie))) {
            waitingCookie.remove(cookie);
            chatService.errorMessage(cookie);
        } else {
            if (connectedUser.get(cookie) == null) {
                if (waitingUser.get(cookie) == null) {
                    addMatchingUser(sessionId, nickname, profileImage, cookie);
                    if (waitingQueue.size() == 2) {
                        String user1SessionId = waitingQueue.poll();
                        String user2SessionId = waitingQueue.poll();

                        String user1NickName = waitingNickname.poll();
                        String user2NickName = waitingNickname.poll();

                        String user1Cookie = waitingCookie.poll();
                        String user2Cookie = waitingCookie.poll();

                        String user1Img = waitingUserImg.poll();
                        String user2Img = waitingUserImg.poll();

                        waitingUser.remove(user1SessionId);
                        waitingUser.remove(user2SessionId);

                        // 방 생성
                        String chatRoomId = UUID.randomUUID().toString();

                        connectedUser.put(user1Cookie, chatRoomId);
                        connectedUser.put(user2Cookie, chatRoomId);

                        if (connectedUser.size() < 2) {
                            chatService.matchingError(user1Cookie, user2Cookie);
                        } else {
                            chatService.matchingSuccess(user1NickName, user2NickName, user1Cookie, user2Cookie, user1Img, user2Img, chatRoomId);
                        }
                    }
                }
            } else {
                gameUser.put(sessionId, connectedUser.get(cookie));
                connectedUser.remove(cookie);
            }
        }
    }

    @EventListener // 연결 종료 이벤트
    public void WebSocketDisconnectedEvent(SessionDisconnectEvent event) throws IOException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) accessor.getHeader("simpSessionId");
        String disconnectRoomId = gameUser.get(sessionId);
        outUser(sessionId);
        chatService.defaultWin(disconnectRoomId);
    }

    public Integer findCookieSize(String roomId) {
        ArrayList<Object> user = new ArrayList<>();
        for (String value : gameUser.values()) {
            if (value.equals(roomId)) {
                user.add(value);
            }
        }
        return user.size();
    }

    public void finish(ChatMessage message) {
        gameUser.remove(message.getCookie());
        connectedUser.remove(message.getCookie());
    }

    private static void addMatchingUser(String sessionId, String nickname, String profileImage, String cookie) {
        waitingQueue.add(sessionId);
        waitingNickname.add(nickname);
        waitingCookie.add(cookie);
        waitingUserImg.add(profileImage);
        waitingUser.put(sessionId, cookie);
    }

    private static void outUser(String sessionId) {
        waitingCookie.remove(waitingUser.get(sessionId));
        connectedUser.remove(waitingUser.get(sessionId));
        waitingNickname.remove(waitingUser.get(sessionId));
        gameUser.remove(sessionId);
        waitingQueue.remove(sessionId);
        waitingUser.remove(sessionId);
    }
}

package com.wolver.mainproject.socket.service;

import com.wolver.mainproject.socket.model.ChatMessage;
import com.wolver.mainproject.socket.model.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messageTamplate;
    private final SimpMessageSendingOperations messageSending;

    public void aloneUser(String roomId, ChatMessage message) throws IOException {
        message.setMessageType(MessageType.DISCONNECTED);
        message.setMessage("상대방이 나갔습니다.  게임을 다시 시작해주세요!");
        messageTamplate.convertAndSend("/sub/chat/enter/" + roomId, message);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.close();
    }

    public void gameChat(String roomId, ChatMessage message) {
        message.setRoomId(roomId);
        messageTamplate.convertAndSend("/sub/chat/enter/" + roomId, message);
    }

    public void gameOut(String roomId, ChatMessage message) throws IOException {
        message.setMessageType(MessageType.DISCONNECTED);
        message.setMessage("게임이 종료되었습니다.");
        messageTamplate.convertAndSend("/sub/chat/enter/" + roomId, message);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.close();
    }

    public void gameFinish(String roomId, ChatMessage message) throws IOException {
        messageTamplate.convertAndSend("/sub/chat/enter/" + roomId, message);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.close();
    }

    public void joinMessage(String chatRoomId, String cookie, String myNickname, String myProfileImage, String otherNickname, String otherProfileImage) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType(MessageType.JOIN);
        chatMessage.setRoomId(chatRoomId);
        chatMessage.setCookie(cookie);
        chatMessage.setMyNickname(myNickname);
        chatMessage.setMyProfileImage(myProfileImage);
        chatMessage.setOtherNickname(otherNickname);
        chatMessage.setOtherProfileImage(otherProfileImage);
        messageSending.convertAndSend("/sub/chat/join/" + cookie, chatMessage);
    }

    public void errorMessage(String cookie) throws IOException {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType(MessageType.ERROR);
        chatMessage.setCookie(cookie);
        messageSending.convertAndSend("/sub/chat/join/" + cookie, chatMessage);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.close();
    }

    public void matchingError(String user1Cookie, String user2Cookie) throws IOException {
        errorMessage(user1Cookie);
        errorMessage(user2Cookie);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.close();
    }

    public void matchingSuccess(String user1NickName, String user2NickName, String user1Cookie, String user2Cookie, String user1Img, String user2Img, String chatRoomId) {
        joinMessage(chatRoomId, user1Cookie, user1NickName, user1Img, user2NickName, user2Img);
        joinMessage(chatRoomId, user2Cookie, user2NickName, user2Img, user1NickName, user1Img);
    }

    public void defaultWin(String disconnectRoomId) throws IOException {
        ChatMessage message = new ChatMessage();
        message.setMessageType(MessageType.VICTORY);
        message.setMessage("상대방이 나갔습니다.");
        messageSending.convertAndSend("/sub/chat/enter/" + disconnectRoomId, message);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.close();
    }
}

package com.wolver.mainproject.socket.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String myNickname;
    private String myProfileImage;
    private String otherNickname;
    private String otherProfileImage;

    private String cookie;
    private String roomId;
    private int quizNumber;
    private String message;
    private MessageType messageType;

}
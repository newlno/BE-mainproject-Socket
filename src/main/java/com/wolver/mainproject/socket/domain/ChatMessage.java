package com.wolver.mainproject.socket.domain;

import lombok.*;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage implements Serializable {

    private String sessionId;
    private String message;
    private String roomId;
    private MessageType messageType;

}
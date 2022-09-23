package com.wolver.mainproject.socket.domain;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage implements Serializable {

    @Id
    private String sessionId;
    private String message;
    private String roomId;
    private MessageType messageType;

}
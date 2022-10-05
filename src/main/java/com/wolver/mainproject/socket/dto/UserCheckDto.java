package com.wolver.mainproject.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserCheckDto {

    private String ticket;
    private String cookie;
}

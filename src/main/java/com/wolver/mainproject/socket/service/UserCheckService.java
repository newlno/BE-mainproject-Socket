package com.wolver.mainproject.socket.service;

import com.wolver.mainproject.socket.dto.UserCheckDto;
import com.wolver.mainproject.socket.util.AES256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCheckService {

    private final AES256 aes256;

    public boolean checkCookies(UserCheckDto userCheckDto) throws Exception {
        String cookie = userCheckDto.getCookie();
        String ticket = userCheckDto.getTicket();
        return cookie.equals(aes256.decrypt(ticket));
    }

}

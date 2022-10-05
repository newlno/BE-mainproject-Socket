package com.wolver.mainproject.socket.controller;

import com.wolver.mainproject.socket.dto.UserCheckDto;
import com.wolver.mainproject.socket.service.UserCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserCheckController {

    private final UserCheckService userCheckService;

    @PostMapping("/api/game/ticket/check")
    public boolean getUserInfo(@RequestBody UserCheckDto userCheckDto) throws Exception {
        return userCheckService.checkCookies(userCheckDto);
    }

}

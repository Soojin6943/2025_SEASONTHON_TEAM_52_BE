package com.roommate.roommate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServerHealthCheck {
    @GetMapping("/health")
    public String health() {
        return "서버 실행중";
    }
}

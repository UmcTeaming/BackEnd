package com.teaming.TeamingServer.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller            // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함
@RequiredArgsConstructor
public class MemberController {
    @GetMapping("/")
    public String Home() {
        return "home";
    }
}

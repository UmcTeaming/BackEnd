package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberServiceImplTest {

    @Autowired
    MemberService memberService;

    @Test
    public void 로그인_기능() {
        String name = "오남의";
        String email = "test@gmail.com";
        String password = "test123";

        MemberRequestDto memberRequestDto = new MemberRequestDto(name, email, password);

        memberService.join(memberRequestDto);

        memberService.login(email, password);
    }
}

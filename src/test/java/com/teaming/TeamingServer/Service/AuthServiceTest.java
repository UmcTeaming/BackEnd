package com.teaming.TeamingServer.Service;


import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AuthService authService;

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    public void 회원가입() {
        String name = "한소희";
        String email = "test@gmail.com";
        String password = "test123";

        MemberRequestDto memberRequestDto = new MemberRequestDto(name, email, password);

        authService.join(memberRequestDto);

        Member findMember = memberRepository.findByEmail(email).stream().findFirst().get();

        assertThat(email).isEqualTo(findMember.getEmail());
    }
}

package com.teaming.TeamingServer.controller;


import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Service.MemberServiceImpl;
import com.teaming.TeamingServer.common.BaseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberControllerTest {

    @Autowired
    MemberController memberController;
    @Autowired
    MemberRepository memberRepository;
    MemberRequestDto memberRequestDto = new MemberRequestDto("홍길동", "test@gmail.com", "test1234", "test1234");

    @Test
    public void 회원가입_정상응답() {
        // given
        BaseResponse signupResponse = memberController.signup(memberRequestDto);

        // when
        BaseResponse expectedResponse = new BaseResponse<>(200, "회원가입이 완료되었습니다.");

        // then
        Assertions.assertEquals(signupResponse.getMessage(), expectedResponse.getMessage());
        Assertions.assertEquals(signupResponse.getStatus(), expectedResponse.getStatus());

        System.out.println("Response : " + signupResponse);
    }
}

package com.teaming.TeamingServer.service;

import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Service.MemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceImplTest {

    @Autowired MemberServiceImpl memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {

        //given
        Member member = Member.builder()
                .name("홍길동")
                .email("test@gmail.com")
                .password("test1234")
                .agreement(true)
                .build();

        //when
        Integer savedId = memberService.join(member);

        //then
        Assertions.assertEquals(member, memberRepository.findById(savedId).get());
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = Member.builder()
                .name("홍길동")
                .email("test@gmail.com")
                .password("test1234")
                .agreement(true)
                .build();

        Member member2 = Member.builder()
                .name("홍길동")
                .email("test@gmail.com")
                .password("test1234")
                .agreement(true)
                .build();

        //when
        memberService.join(member1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.join(member2);
        }); //예외가 발생해야 한다!!!
    }

}

package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 밑에 MemberRepository 의 생성자를 쓰지 않기 위해
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Integer join(MemberRequestDto memberRequestDto) {
        // password 체크
        if(memberRequestDto.getPassword() != memberRequestDto.getCheckPassword()) {
            // 오류 발생..?
        }
        Member member = Member.builder()
                .name(memberRequestDto.getName())
                .email(memberRequestDto.getEmail())
                .password(memberRequestDto.getPassword())
                .build();

        return memberRepository.save(member).getMember_id();
    }
}

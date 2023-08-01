package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.CheckCurrentPasswordRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;


    @Override
    public ResponseEntity changePassword() {
        return null;
    }

    @Override
    public ResponseEntity checkCurrentPassword(Long memberId, CheckCurrentPasswordRequestDto checkCurrentPasswordRequestDto) {
        // 1. DB 에서 ID 로 회원 객체 조회 후 존재하는 회원인지 체크
        List<Member> findMember = memberRepository.findById(memberId).stream().toList();

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. 사용자가 입력한 비밀번호와 DB 에 있는 비밀번호가 일치하는지 확인
        String currentPassword = findMember.stream().findFirst().stream().toList().stream().findFirst().get().getPassword();

        if (!currentPassword.equals(checkCurrentPasswordRequestDto.getCurrentPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "알맞은 비밀번호를 입력해주세요."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "비밀번호가 일치합니다."));
    }

    @Override
    public ResponseEntity MemberMyPage() {
        return null;
    }
}

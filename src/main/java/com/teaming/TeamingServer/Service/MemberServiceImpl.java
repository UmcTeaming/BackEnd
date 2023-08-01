package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.Dto.CheckCurrentPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberChangePasswordDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public ResponseEntity changePassword(Long memberId, MemberChangePasswordDto memberChangePasswordDto) {
        // 1. memberId 를 가진 멤버가 존재하는지 확인
        Optional<Member> findMember = memberRepository.findById(memberId);
        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. 존재한다면, 비밀번호 변경 후 로그인과 똑같이 인증정보 재발급
        Authentication authentication = null;

        // (1) 비밀번호 변경
        Member member = findMember.get();

        member.updatePassword(memberChangePasswordDto.getChange_password());

        try {
            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        }
        catch (IllegalArgumentException | AuthenticationException illegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), illegalArgumentException.getMessage()));
        }

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        JwtToken newToken = JwtToken.builder()
                    .grantType(token.getGrantType())
                    .accessToken(token.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .memberId(memberId)
                    .build();


        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<JwtToken>(HttpStatus.OK.value(), "비밀번호 변경이 완료되었습니다.", newToken));
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

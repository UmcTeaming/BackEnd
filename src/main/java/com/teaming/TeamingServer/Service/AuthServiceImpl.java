package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProviderImpl;
import com.teaming.TeamingServer.Domain.Dto.request.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.request.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.response.MemberLoginResponse;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BadRequestException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Service.Utils.RedisUtil;
import com.teaming.TeamingServer.common.KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final RedisUtil redisUtil;

    // email 인증 코드
    private String emailCode;

    // jwt
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProviderImpl jwtTokenProviderImpl;


    /**
     * 회원 가입
     */
    @Transactional
    @Override
    public void join(MemberRequestDto memberRequestDto) {
        validateMemberRequest(memberRequestDto);
        validateDuplicateEmail(memberRequestDto.getEmail());
        memberRepository.save(new Member(memberRequestDto));
    }

    @Transactional
    @Override
    public void verifyEmailCode(String inputCode) {
        if(isEmailVerified(inputCode)) return;
        throw new BadRequestException("인증번호가 일치하지 않습니다.");
    }

    @Transactional(readOnly = true)
    @Override
    public MemberLoginResponse login(String email, String password) {
        Member member = getMember(email);
        member.validatePassword(password);
        JwtToken jwtToken = jwtTokenProviderImpl.generateToken(member);

        // 로그인 시, BlackList 에 있는 토큰을 삭제해주기 : JWT 토큰은 세션 정보가 바뀌면 새로 바뀌는 것이 아니기 때문에 추가해줘야 로그인 후 발급된 토큰을 사용할 수 있음!
        if(redisUtil.hasKeyBlackList(jwtToken.getAccessToken())) {
            redisUtil.delete(jwtToken.getAccessToken());
        }

        return new MemberLoginResponse(member.getName(), jwtToken);
    }

    @Transactional
    @Override
    public void resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) {
        Member member = getMember(memberResetPasswordRequestDto.getEmail()).setRandomPassword();
        try {
            emailServiceImpl.sendResetPasswordMessage(member.getEmail(), member.getPassword());
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void validateEmailRequest(String email) {
        validateDuplicateEmail(email);
        createVerificationCode();
        emailServiceImpl.sendValidateEmailRequestMessage(email, getVerificationCode());
    }

    private boolean isEmailVerified(String inputCode) {
        return emailCode.equals(inputCode);
    }

    private void createVerificationCode() {
        emailCode = KeyGenerator.createKey();
    }

    private String getVerificationCode() {
        return emailCode;
    }

    private void validateDuplicateEmail(String email) {
        if(memberRepository.findByEmail(email).stream().toList().size() > 0) {
            throw new BadRequestException("이미 회원가입된 이메일입니다.");
        }
    }

    private void validateMemberRequest(MemberRequestDto memberRequestDto) {
        if(memberRequestDto.getName() == null) throw new BadRequestException("이름을 입력해주세요.");
        if(memberRequestDto.getEmail() == null) throw new BadRequestException("이메일을 입력해주세요.");
        if(memberRequestDto.getPassword() == null) throw new BadRequestException("비밀번호를 입력해주세요.");
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("회원가입되지 않은 이메일입니다."));
    }
}

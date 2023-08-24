package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.Dto.MemberLoginResponse;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BadRequestException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor // 밑에 MemberRepository 의 생성자를 쓰지 않기 위해
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // email 인증 코드
    private String emailCode;

    // jwt
    private final JwtTokenProvider jwtTokenProvider;


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
        if (isEmailVerified(inputCode)) return;
        throw new BadRequestException("인증번호가 일치하지 않습니다.");
    }

    @Transactional(readOnly = true)
    @Override
    public MemberLoginResponse login(String email, String password) {
        Member member = getMember(email);
        member.validatePassword(password);
        return new MemberLoginResponse(member.getName(), jwtTokenProvider.generateToken(member));
    }

    @Transactional
    @Override
    public void resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception {
        Member member = getMember(memberResetPasswordRequestDto.getEmail()).setRandomPassword();
        emailService.sendResetPasswordMessage(member);
    }

    @Transactional
    @Override
    public void validateEmailRequest(String email) {
        validateDuplicateEmail(email);
        createVerificationCode();
        emailService.sendValidateEmailRequestMessage(email, getVerificationCode());
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
        if (memberRepository.findByEmail(email).size() > 0) {
            throw new BadRequestException("이미 회원가입된 이메일입니다.");
        }
    }

    private void validateMemberRequest(MemberRequestDto memberRequestDto) {
        if (memberRequestDto.getName() == null) throw new BadRequestException("이름을 입력해주세요.");
        if (memberRequestDto.getEmail() == null) throw new BadRequestException("이메일을 입력해주세요.");
        if (memberRequestDto.getPassword() == null) throw new BadRequestException("비밀번호를 입력해주세요.");
    }


    private Member getMember(String email) {
        return memberRepository.findByEmail(email).stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("회원가입되지 않은 이메일입니다."));
    }
}

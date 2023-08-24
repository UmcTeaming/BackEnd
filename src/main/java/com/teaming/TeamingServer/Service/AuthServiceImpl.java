package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.Dto.MemberLoginResponse;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BadRequestException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

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
    public BaseResponse join(MemberRequestDto memberRequestDto) {
        validateMemberRequest(memberRequestDto);
        validateDuplicateEmail(memberRequestDto.getEmail());
        memberRepository.save(new Member(memberRequestDto));
        return new BaseResponse("회원가입이 완료되었습니다.");
    }

    @Transactional
    @Override
    public ResponseEntity verificationEmail(String inputCode) {
        verifyEmail(inputCode);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse("사용자 이메일 인증 성공"));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity login(String email, String password) {
        getMemberLoginResponse(email, password);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse("로그인 성공"));
    }

    @Transactional
    @Override
    public ResponseEntity resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception {
        Member member = getMember(memberResetPasswordRequestDto.getEmail()).setRandomPassword();
        emailService.sendResetPasswordMessage(member);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse("비밀번호 재설정 메일이 발송되었습니다."));
    }

    @Transactional
    @Override
    public ResponseEntity validateEmailRequest(String email) throws Exception {
        validateDuplicateEmail(email);

        emailCode = createKey();
        // 이메일 인증 번호 발급
        emailService.sendValidateEmailRequestMessage(email, emailCode);

        // 이메일 검증 및 전송 정상 통과
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse("사용 가능한 이메일입니다."));
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).size() > 0) {
            throw new BadRequestException("이미 회원가입된 이메일입니다.");
        }
    }

    private void validateMemberRequest(MemberRequestDto memberRequestDto) {
        if ((memberRequestDto.getName() != null)
                && (memberRequestDto.getEmail() != null)
                && (memberRequestDto.getPassword() != null))
            return;

        throw new BadRequestException("회원가입 정보를 모두 입력해주세요.");

    }


    private void verifyEmail(String inputCode) {
        if (!inputCode.equals(emailCode))
            throw new BadRequestException("인증번호가 일치하지 않습니다.");
    }

    private MemberLoginResponse getMemberLoginResponse(String email, String password) {
        Member member = getMatchedMember(email, password);
        return MemberLoginResponse.builder()
                .name(member.getName())
                .jwtToken(jwtTokenProvider.generateToken(member))
                .build();
    }

    private Member getMatchedMember(String email, String password) {
        return memberRepository.findByEmail(email).stream()
                .filter(member -> member.isPasswordMatched(password))    // 암호화된 비밀번호와 비교하도록 수정
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("회원가입되지 않은 이메일입니다."));
    }
}

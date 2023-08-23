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
import org.apache.commons.lang3.RandomStringUtils;
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
    public ResponseEntity join(MemberRequestDto memberRequestDto) {
        validateMemberRequest(memberRequestDto);
        validateDuplicateEmail(memberRequestDto.getEmail());
        memberRepository.save(mapToMember(memberRequestDto));
        return getSuccessResponse("회원가입이 완료되었습니다.", null);
    }

    @Transactional
    @Override
    public ResponseEntity verificationEmail(String inputCode) {
        verifyEmail(inputCode);
        return getSuccessResponse("사용자 이메일 인증 성공", null);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity login(String email, String password) {
        return getSuccessResponse("로그인 성공", getMemberLoginResponse(email, password));
    }

    @Transactional
    @Override
    public ResponseEntity resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception {
        Member member = getMember(memberResetPasswordRequestDto.getEmail());
        member.updatePassword(createRandomPassword());
        emailService.sendResetPasswordMessage(member);
        return getSuccessResponse("비밀번호 재설정 메일이 발송되었습니다.", null);
    }

    @Transactional
    @Override
    public ResponseEntity validateEmailRequest(String email) throws Exception {
        validateDuplicateEmail(email);

        emailCode = createKey();
        // 이메일 인증 번호 발급
        emailService.sendValidateEmailRequestMessage(email, emailCode);

        // 이메일 검증 및 전송 정상 통과
        return getSuccessResponse("사용 가능한 이메일입니다.", null);
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

    private static Member mapToMember(MemberRequestDto memberRequestDto) {
        return Member.builder()
                .name(memberRequestDto.getName())
                .email(memberRequestDto.getEmail())
                .password(memberRequestDto.getPassword())
                .agreement(true).build();
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


    private static ResponseEntity<BaseResponse<Object>> getSuccessResponse(String message, Object object) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(), message));
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


    // 랜덤 비밀번호 만들기
    private static String createRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("회원가입되지 않은 이메일입니다."));
    }
}

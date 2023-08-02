package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
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

@Service
@Transactional
@RequiredArgsConstructor // 밑에 MemberRepository 의 생성자를 쓰지 않기 위해
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // email 인증 코드
    private String emailCode;

    // jwt
//    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 회원 가입
     */
    @Transactional
    @Override
    public ResponseEntity join(MemberRequestDto memberRequestDto) {

        // 회원가입 정보 모두 입력 체크
        if(!checkBlank(memberRequestDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "회원가입에 필요한 모든 데이터를 입력해주세요."));
        }

        Member member = Member.builder()
                .name(memberRequestDto.getName())
                .email(memberRequestDto.getEmail())
                .password(memberRequestDto.getPassword())
                .agreement(true).build();

        // 중복 회원 검증
        if(!checkDuplicateEmail(member.getEmail())) {
            throw new IllegalArgumentException("이미 회원가입된 이메일입니다.");
        };

//        // 비밀번호 암호화
//        String encPwd = encoder.encode(member.getPassword());
//        member.setPassword(encPwd);

        // 이메일 인증

        // 회원 DB 에 저장
        memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(), "회원가입이 완료되었습니다."));
    }

    @Transactional
    @Override
    public ResponseEntity validateDuplicateMember(MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception {
        // 이메일 중복 체크
        if(!checkDuplicateEmail(memberSignUpEmailDuplicationRequestDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "이미 회원가입된 이메일입니다."));
        }

        // 이메일 인증 번호 발급
        emailCode = mailConfirm(memberSignUpEmailDuplicationRequestDto.getEmail());

        // 이메일 검증 및 전송 정상 통과
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(), "사용 가능한 이메일입니다."));
    }

    @Transactional
    @Override
    public ResponseEntity verificationEmail(MemberVerificationEmailRequestDto memberVerificationEmailRequestDto) {
        if(checkCode(memberVerificationEmailRequestDto.getAuthentication(), emailCode)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "사용자 이메일 인증 성공"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public JwtToken login(String email, String password) {

        Authentication authentication = null;

        Long memberId = null;

        try {
            // DB 에 계정이 있는지와 그 계정과 이메일, 비밀번호가 일치한지
           Member findMember = memberRepository.findByEmail(email).stream().filter(it -> password.equals(it.getPassword()))	// 암호화된 비밀번호와 비교하도록 수정
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

           memberId = findMember.getMember_id();

            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        }
        catch (IllegalArgumentException | AuthenticationException illegalArgumentException) {
            return null;
        }

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        return JwtToken.builder()
                .grantType(token.getGrantType())
                .accessToken(token.getAccessToken())
                .memberId(memberId)
                .build();
    }

    @Transactional
    @Override
    public ResponseEntity resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception {

        // 1. 이메일이 회원 DB 에 있는지 체크 한다.
        List<Member> findMember = memberRepository.findByEmail(memberResetPasswordRequestDto.getEmail());

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "회원가입되지 않은 이메일입니다."));
        }

        try {
            // 2. 회원가입된 이메일이라면, 랜덤 비밀번호를 이메일로 보낸 뒤 DB 에 반영한다.
            String resetPassword = passwordResetMailConfirm(memberResetPasswordRequestDto.getEmail());
            findMember.stream().findFirst().get().setPassword(resetPassword);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
        }


        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "비밀번호가 재설정되었습니다."));

    }



    private boolean checkCode(String authentication, String emailCode) {
        return authentication.equals(emailCode);
    }

    private String mailConfirm(String email) throws Exception {
        String code = emailService.sendSimpleMessage(email);
        // log.info("인증코드 : " + code);
        return code;
    }

    private String passwordResetMailConfirm(String email) throws Exception {
        String resetPassword = emailService.sendResetPasswordMessage(email);
        // log.info("인증코드 : " + code);
        return resetPassword;
    }

    private boolean checkDuplicateEmail(String email) {
        List<Member> findMember = memberRepository.findByEmail(email);

        return findMember.isEmpty();
    }

    private boolean checkBlank(MemberRequestDto memberRequestDto) {
        if((memberRequestDto.getName() == null)
                || (memberRequestDto.getEmail() == null)
                || (memberRequestDto.getPassword() == null)) {
            return false;
        }

        return true;
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long id) {
        return memberRepository.findById(id).get();
    }

    /**
     * 회원 수정 : 프로필 사진 업데이트 + 비밀번호
     */
    @Transactional
    public void updateProfileImage(Long id, String profile_image) {
        Member member = (memberRepository.findById(id)).get();
        member.update(profile_image);
    }
}

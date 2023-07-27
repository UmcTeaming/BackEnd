package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.Role;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

// @Slf4j
@Service
@RequiredArgsConstructor // 밑에 MemberRepository 의 생성자를 쓰지 않기 위해
public class MemberServiceImpl implements MemberService {

    // @Autowired
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // email 인증 코드
    private String emailCode;

    // jwt
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private JwtTokenProvider jwtTokenProvider;

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
                .role(Role.valueOf("MEMBER"))
                .agreement(true).build();

        // 중복 회원 검증
        if(!checkDuplicateEmail(member.getEmail())) {
            throw new IllegalArgumentException("이미 회원가입된 이메일입니다.");
        };

        // 비밀번호 일치 검증

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
    public ResponseEntity verificationEmail(MemberVerificationEmailRequestDto memberVerificationEmailRequestDto) {
        if(checkCode(memberVerificationEmailRequestDto.getAuthentication(), emailCode)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "사용자 이메일 인증 성공"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다."));
    }

    @Transactional
    public JwtToken login(String email, String password) {
        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    private boolean checkCode(String authentication, String emailCode) {
        return authentication.equals(emailCode);
    }

    private String mailConfirm(String email) throws Exception {
        String code = emailService.sendSimpleMessage(email);
        // log.info("인증코드 : " + code);
        return code;
    }

    private boolean checkDuplicateEmail(String email) {
        List<Member> findMembers = memberRepository.findByEmail(email);
        return findMembers.isEmpty();
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
     * 회원 수정
     */
    @Transactional
    public void update(Long id, String profile_image) {
        Member member = (memberRepository.findById(id)).get();
        member.update(profile_image);
    }
}

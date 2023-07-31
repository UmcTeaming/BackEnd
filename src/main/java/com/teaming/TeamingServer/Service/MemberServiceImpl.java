package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Config.Redis.RedisUtil;
import com.teaming.TeamingServer.Domain.Dto.MemberLogoutRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor // 밑에 MemberRepository 의 생성자를 쓰지 않기 위해
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // email 인증 코드
    private String emailCode;

    // jwt
//    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    // redis - logout 기능 구현을 위해
    private final RedisUtil redisUtil;


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
    public ResponseEntity verificationEmail(MemberVerificationEmailRequestDto memberVerificationEmailRequestDto) {
        if(checkCode(memberVerificationEmailRequestDto.getAuthentication(), emailCode)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "사용자 이메일 인증 성공"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "인증번호가 일치하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public JwtToken login(String email, String password) {

        Authentication authentication = null;

        Long memberId = null;

        Member findMember = null;

        try {
            // DB 에 계정이 있는지와 그 계정과 이메일, 비밀번호가 일치한지
           findMember = memberRepository.findByEmail(email).stream().filter(it -> password.equals(it.getPassword()))	// 암호화된 비밀번호와 비교하도록 수정
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

        //redis에 RT:13@gmail.com(key) / 23jijiofj2io3hi32hiongiodsninioda(value) 형태로 리프레시 토큰 저장하기
        // redisTemplate.opsForValue().set("RT:"+findMember.getEmail(),token.getRefreshToken(),System.currentTimeMillis() + 1000 * 60 * 30, TimeUnit.MILLISECONDS);
        // Redis 로그아웃 방식
        // redisUtil.set(findMember.getEmail(), token.getRefreshToken(), 5);


        return JwtToken.builder()
                .grantType(token.getGrantType())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .memberId(memberId)
                .build();
    }

    // Redis BlackList 방법
//    @Transactional
//    public void logout(MemberLogoutRequestDto memberLogoutRequestDto){
//        // 로그아웃 하고 싶은 토큰이 유효한 지 먼저 검증하기
//        if (!jwtTokenProvider.validateToken(memberLogoutRequestDto.getAccessToken())){
//            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
//        }
//
//        // Access Token에서 User email을 가져온다
//        Authentication authentication = jwtTokenProvider.getAuthentication(memberLogoutRequestDto.getAccessToken());
//
//        // Redis에서 해당 User email로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
//        if (redisUtil.hasKeyBlackList(memberLogoutRequestDto.getRefreshToken())){
//            // Refresh Token을 삭제
//            redisUtil.delete(memberLogoutRequestDto.getRefreshToken());
//        }
//
//        // 레디스에 accessToken 사용못하도록 등록
//        redisUtil.setBlackList(memberLogoutRequestDto.getAccessToken(), "accessToken", 5);
//        redisUtil.setBlackList(memberLogoutRequestDto.getRefreshToken(), "refreshToken", 5);
//    }

    // 토큰 유효기간 변경하는 방법
    @Transactional
    public JwtToken logout(MemberLogoutRequestDto memberLogoutRequestDto) {
        // 로그아웃 하고 싶은 토큰이 유효한지 검증
        if(!jwtTokenProvider.validateToken(memberLogoutRequestDto.getAccessToken()) || !jwtTokenProvider.validateToken(memberLogoutRequestDto.getRefreshToken())) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 유효하다면, 로그아웃을 진행한다.
        JwtToken token = JwtToken.builder()
                .grantType(memberLogoutRequestDto.getGrantType())
                .memberId(memberLogoutRequestDto.getMemberId())
                .accessToken(memberLogoutRequestDto.getAccessToken())
                .refreshToken(memberLogoutRequestDto.getRefreshToken())
                .build();

        return jwtTokenProvider.invalidateToken(token);

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
     * 회원 수정
     */
    @Transactional
    public void updateProfileImage(Long id, String profile_image) {
        Member member = (memberRepository.findById(id)).get();
        member.update(profile_image);
    }
}

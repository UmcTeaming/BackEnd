package com.teaming.TeamingServer.Config.Jwt;

import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final Long expiration = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30; // 유효 기간 한달!
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, AuthenticationManagerBuilder authenticationManagerBuilder, MemberRepository memberRepository) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.memberRepository = memberRepository;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Access token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("expiration = " + new Date(expiration));

//        // Refresh token 생성
//        String refreshToken = Jwts.builder()
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 36))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    public JwtToken generateToken(Member member) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(member.getAuthenticationToken());

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Access token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("expiration = " + new Date(expiration));

//        // Refresh token 생성
//        String refreshToken = Jwts.builder()
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 36))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .memberId(member.getMemberId())
                .build();
    }


    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

//        // Member 의 권한 별로 접근 가능 페이지가 다를 때 ex) 관리자, 이용자 등
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get("auth").toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());

        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }

        return false;
    }


    // MemberId 검증
    public void checkMemberId(Authentication authentication, HttpServletRequest request) {
        String tokenEmail = authentication.getName();

        // Extract MemberID from the request
        // '/projects/{memberId}/{projectId}/files-upload'
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        String[] parts = requestURI.split("/");

        // /auth ~ 기능들인지 확인
        if (checkApiURL(parts)) {
            return;
//            throw new BaseException(HttpStatus.FORBIDDEN.value(), "auth 에 속한 기능들은 authorization 이 필요하지 않습니다.");
        }

        Long memberId = getMemberId(parts);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.FORBIDDEN.value(), "유효하지 않은 회원 ID"));

        if (!tokenEmail.equals(member.getEmail())) {
            throw new BaseException(HttpStatus.FORBIDDEN.value(), "유효하지 않은 AccessToken");
        }
    }

    // api/~ 이렇게 요청이 들어오면 parts[2] 를 검사
    // auth/~ 이렇게 요청이 들어오면 parts[1] 을 검사
    private boolean checkApiURL(String[] parts) {
        if (parts[1].equals("api")) {
            return parts[2].equals("auth");
        }
        return parts[1].equals("auth");
    }

    private Long getMemberId(String[] parts) {
        if (parts[1].equals("api")) {
            return Long.parseLong(parts[3]);
        }

        return Long.parseLong(parts[2]);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key)
                    .build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
package com.teaming.TeamingServer.Config.Jwt;

import com.teaming.TeamingServer.Config.Redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private RedisUtil redisUtil;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
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
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 36))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            // 추가된 부분
            if (redisUtil.hasKeyBlackList(token)){
                throw new RuntimeException("로그아웃 되었습니다. 다시 로그인해주세요.");
            }
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

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key)
                    .build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // JwtTokenProvider에 유효기간을 변경하는 메서드를 추가합니다.
    public JwtToken invalidateToken(JwtToken token) {
        // 토큰을 파싱하여 Claims 객체를 얻어옵니다.
        Claims accessClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.getAccessToken()).getBody();
        Claims refreshClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.getRefreshToken()).getBody();

        // 토큰의 만료시간을 현재 시간보다 이전으로 설정하여 토큰을 무효화합니다.
        accessClaims.setExpiration(new Date(System.currentTimeMillis() - 1));
        refreshClaims.setExpiration(new Date(System.currentTimeMillis() -1));

        // 변경된 Claims로 새로운 토큰을 생성합니다.
        String newAccessToken = Jwts.builder()
                .setClaims(accessClaims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String newRefreshToken = Jwts.builder()
                .setClaims(refreshClaims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 새로운 토큰을 클라이언트에게 전달하거나, 필요한 처리를 진행합니다.
        // 예: response.setHeader("Authorization", "Bearer " + newToken);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .accessToken(newRefreshToken)
                .build();
    }
}
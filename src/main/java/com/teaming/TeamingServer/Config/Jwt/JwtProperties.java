package com.teaming.TeamingServer.Config.Jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class JwtProperties {
    public int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
    public String TOKEN_PREFIX = "Bearer ";
    public String HEADER_STRING = "Authorization";
    @Value("${jwt.secret}")
    public String secret;
}

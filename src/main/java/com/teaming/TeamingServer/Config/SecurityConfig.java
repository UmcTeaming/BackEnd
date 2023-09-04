package com.teaming.TeamingServer.Config;

import com.teaming.TeamingServer.Config.Jwt.JwtAuthenticationFilter;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProviderImpl jwtTokenProviderImpl;

    private String[] possibleAccess = {"/api/auth/signup"
            , "/api/auth/email-duplication", "/api/auth/email-verification", "/api/auth/login"
            , "/api/auth/reset-password", "/api/error", "/api", "/error", "/auth/**"};

    public SecurityConfig(JwtTokenProviderImpl jwtTokenProviderImpl, RedisTemplate redisTemplate) {
        this.jwtTokenProviderImpl = jwtTokenProviderImpl;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // h2 접근 허용
        http
                .headers((header) -> header.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));
        http
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/h2-console/**").disable());

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                                .requestMatchers(HttpMethod.POST, possibleAccess).permitAll()
                                .requestMatchers(HttpMethod.GET, possibleAccess).permitAll()
                                .requestMatchers(HttpMethod.PUT, possibleAccess).permitAll()
                                .requestMatchers(HttpMethod.DELETE, possibleAccess).permitAll()
                                .requestMatchers(HttpMethod.PATCH, possibleAccess).permitAll()
                                .anyRequest().authenticated()
                );
//        http
//                .headers((header) -> header.addHeaderWriter(new StaticHeadersWriter("Access-Control-Expose-Headers", "ContentDisposition")));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProviderImpl), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

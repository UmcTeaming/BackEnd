package com.teaming.TeamingServer.Config;

import com.teaming.TeamingServer.Config.Jwt.JwtAuthenticationFilter;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Config.LogoutHandler.CustomLogoutSuccessHandler;
import com.teaming.TeamingServer.Config.Redis.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, RedisUtil redisUtil) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisUtil = redisUtil;
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
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
//                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests.anyRequest().permitAll());

        http.authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                        .requestMatchers("/member/signup", "/login", "/error").permitAll() // 회원가입, 로그인은 인증 없이 접근 가능하도록 설정
                        .anyRequest().authenticated()); // 그 외의 요청은 인증을 필요로 함
        // .formLogin((formLogin) -> formLogin.loginProcessingUrl("/login"))
        // .authorizeHttpRequests((antMatchers) -> antMatchers.requestMatchers("/member/").permitAll())

//        http.csrf((csrf) -> csrf.ignoringRequestMatchers("/logout", "/member/signup", "/login", "/error"));

//        http.logout((logout) ->
//                logout
//                .logoutUrl("/logout"));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningLogoutSuccessHandler(); // 로그아웃 후에도 POST 방식으로 리다이렉트
    }
}

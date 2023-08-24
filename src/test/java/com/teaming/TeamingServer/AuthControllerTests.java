package com.teaming.TeamingServer;

import com.teaming.TeamingServer.Controller.AuthController;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Service.AuthService;
import com.teaming.TeamingServer.Service.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTests {
    FakeMemberRepository fakeMemberRepository;
    SpyEmailService spyEmailService;
    SpyJwtTokenProvider spyJwtTokenProvider;
    AuthService authService;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        fakeMemberRepository = new FakeMemberRepository();
        spyEmailService = new SpyEmailService();
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        authService = new AuthServiceImpl(fakeMemberRepository, spyEmailService, spyJwtTokenProvider);
        AuthController authController = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void signup_returnsBadRequestStatus_whenNameIsMissing() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"email\": \"test@gmail.com\",\n" +
                                "    \"password\": \"test1234\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("이름을 입력해주세요.")))
        ;
    }

    @Test
    void signup_returnsBadRequestStatus_whenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"tester\",\n" +
                                "    \"password\": \"test1234\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("이메일을 입력해주세요.")))
        ;
    }

    @Test
    void signup_returnsBadRequestStatus_whenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"tester\",\n" +
                                "    \"email\": \"test@gmail.com\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("비밀번호를 입력해주세요.")))
        ;
    }

    @Test
    void signup_returnsBadRequestStatus_whenEmailIsDuplicated() throws Exception {
        fakeMemberRepository.save(Member.builder().email("test@gmail.com").build());
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"tester\",\n" +
                                "    \"email\": \"test@gmail.com\",\n" +
                                "    \"password\": \"test1234\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("이미 회원가입된 이메일입니다.")))
        ;
    }

    @Test
    void signup_returnsOkStatus() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"tester\",\n" +
                                "    \"email\": \"test@gmail.com\",\n" +
                                "    \"password\": \"test1234\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo("회원가입이 완료되었습니다.")))
        ;
    }

    @Test
    void signup_persistsMemberInfo() throws Exception {
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"tester\",\n" +
                        "    \"email\": \"test@gmail.com\",\n" +
                        "    \"password\": \"test1234\"\n" +
                        "}"));

        List<Member> result = fakeMemberRepository.findByEmail("test@gmail.com");
        Member expected = Member.builder()
                .name("tester")
                .email("test@gmail.com")
                .password("test1234")
                .build();
        assertThat(result).hasSize(1).contains(expected);
    }
}

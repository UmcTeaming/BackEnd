package com.teaming.TeamingServer.Domain.entity;


import com.teaming.TeamingServer.Domain.Dto.request.MemberRequestDto;
import com.teaming.TeamingServer.Exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Member extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String profile_image;

    @Column(nullable = false)
    private boolean agreement;

//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MemberProject> memberProjects = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MemberSchedule> memberSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<File> files = new ArrayList<>();


    // 여기도 스케줄 관련 추가해야줘야하나??

    @Builder
    public Member(String name, String email, String password, boolean agreement) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.agreement = agreement;
    }

    public Member(MemberRequestDto dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
    }

    public Member updateProfileImage(String profile_image) {
        this.profile_image = profile_image;
        return this;
    }

    public Member updatePassword(String password) {
        this.password = password;
        return this;
    }

    public Member updateNickName(String nickName) {
        this.name = nickName;
        return this;
    }

    public Member updateMemberProject(MemberProject memberProject) {
        this.memberProjects.add(memberProject);
        return this;
    }

    public Member setRandomPassword() {
        this.password = RandomStringUtils.randomAlphanumeric(10);
        return this;
    }

    public boolean isPasswordMatched(String password) {
        return this.password.equals(password);
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

    public long getMemberId() {
        return member_id;
    }

    public void validatePassword(String password) {
        if(!isPasswordMatched(password)) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }
    }
}
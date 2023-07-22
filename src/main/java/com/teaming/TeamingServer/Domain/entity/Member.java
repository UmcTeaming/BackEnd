package com.teaming.TeamingServer.Domain.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Member extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int member_id;

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

    @OneToMany(mappedBy ="member")
    public List<MemberProject> memberProjectList = new ArrayList<>();

    @OneToMany(mappedBy="member")
    public List<MemberSchedule> memberSchedules = new ArrayList<>();



    @Builder
    public Member(String name, String email, String password, boolean agreement) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.agreement = agreement;
    }

    public Member update(String profile_image) {
        this.profile_image = profile_image;
        return this;
    }
}
package com.teaming.TeamingServer.Domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

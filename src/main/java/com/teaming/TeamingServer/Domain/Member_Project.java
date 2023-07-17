package com.teaming.TeamingServer.Domain;

import jakarta.persistence.*;

@Entity
public class Member_Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int member_project_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}

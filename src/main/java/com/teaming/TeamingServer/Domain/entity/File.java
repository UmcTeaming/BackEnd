package com.teaming.TeamingServer.Domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class File extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int file_id;

    @Column(nullable = false)
    private String file_link;

    @Column(nullable = false)
    private Boolean file_status;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

}

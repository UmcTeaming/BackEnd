package com.teaming.TeamingServer.Domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Project extends Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int project_id;

    @Column(nullable = false)
    private String project_name;

    @Column(nullable = false)
    private String project_image;

    @Column(nullable = false)
    private LocalDate start_date;

    @Column(nullable = false)
    private LocalDate end_date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status project_status;

    @Column(nullable = false)
    private String project_color;

    @OneToMany(mappedBy="project")
    public List<MemberProject> members = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    public List<Schedule> schedules = new ArrayList<>();

    @Builder
    public Project(String project_name, LocalDate start_date, LocalDate end_date, Status project_status) {
        this.project_name = project_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.project_status = project_status;
    }

    public Project update(String profile_image, String project_color) {
        this.project_image = profile_image;
        this.project_color = project_color;

        return this;
    }
}
package com.teaming.TeamingServer.Domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;

@Getter
@NoArgsConstructor
@Entity
public class Project extends Time {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long project_id;

    @Column(nullable = false)
    private String project_name;

    @Column
    private String project_image;

    @Column(nullable = false)
    private LocalDate start_date;

    @Column(nullable = false)
    private LocalDate end_date;

    @Column
    @Enumerated(EnumType.STRING)
    private Status project_status;

    @Column(nullable = false)
    private String project_color;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MemberProject> memberProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Schedule> schedules = new ArrayList<>();

    @Builder
    public Project(String project_name, LocalDate start_date, LocalDate end_date, Status project_status, String project_color, String project_image) {
        this.project_name = project_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.project_status = project_status;
        this.project_color = project_color;
        this.project_image = project_image;
    }

    public Project modifyProject(String project_name, LocalDate start_date, LocalDate end_date, String project_color, String project_image) {
        this.project_name = project_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.project_color = project_color;
        this.project_image = project_image;

        return this;
    }

    public Project updateEndDate(LocalDate endDate) {
        this.end_date = endDate;
        return this;
    }

    public Project updateProfile(String profile_image) {
        this.project_image = profile_image;

        return this;
    }

    public Project updateColor(String project_color) {
        this.project_color = project_color;

        return this;
    }

    public Project updateStatus(Status project_status) {
        this.project_status = project_status;

        return this;
    }
}

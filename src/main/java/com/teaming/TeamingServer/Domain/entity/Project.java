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
    private int project_id;

    @Column
    private String project_name;

    @Column
    private String project_image;

    @Column
    private LocalDate start_date;

    @Column
    private LocalDate end_date;

// 이렇게 해야 돌아가서 수정했어
@Column
@Enumerated(EnumType.STRING)
private Status project_status;

    @Column
    private String project_color;

    @OneToMany(mappedBy="project")
    public List<MemberProject> members = new ArrayList<>();

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

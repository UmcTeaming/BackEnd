package com.teaming.TeamingServer.Domain;

import com.teaming.TeamingServer.Domain.type.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Project {
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
    @Builder.Default()
    @Column(nullable = false)
    private Status project_status = Status.ING;

    @Column(nullable = false)
    private String project_color;

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

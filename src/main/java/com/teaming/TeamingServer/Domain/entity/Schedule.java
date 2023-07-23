package com.teaming.TeamingServer.Domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Schedule extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int schedule_id;

    @Column(nullable = false)
    private String schedule_name;

    @Column(nullable = false)
    private LocalDate schedule_start;

    @Column(nullable = false)
    private LocalDate schedule_end;

    @Column(nullable = false)
    private LocalTime schedule_start_time;

    @Column(nullable = false)
    private LocalTime schedule_end_time;

    @Column(nullable = false)
    private String memo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy="schedule")
    public List<MemberSchedule> members = new ArrayList<>();

    @Builder
    public Schedule(String schedule_name, LocalDate schedule_start
            , LocalDate schedule_end, LocalTime schedule_start_time
            , LocalTime schedule_end_time, String memo) {
        this.schedule_name = schedule_name;
        this.schedule_start = schedule_start;
        this.schedule_end = schedule_end;
        this.schedule_start_time = schedule_start_time;
        this.schedule_end_time = schedule_end_time;
        this.memo = memo;
    }

    public Schedule update(String schedule_name, LocalDate schedule_start
            , LocalDate schedule_end, LocalTime schedule_start_time
            , LocalTime schedule_end_time, String memo) {
        this.schedule_name = schedule_name;
        this.schedule_start = schedule_start;
        this.schedule_end = schedule_end;
        this.schedule_start_time = schedule_start_time;
        this.schedule_end_time = schedule_end_time;
        this.memo = memo;

        return this;
    }
}

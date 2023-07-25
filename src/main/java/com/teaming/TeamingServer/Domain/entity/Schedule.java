package com.teaming.TeamingServer.Domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
//@EnableJpaAuditing  //여기 맞나요..?
@Entity
public class Schedule extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int schedule_id;

    @Column(nullable = false)
    private String schedule_name;

//    @Temporal(value = TemporalType.TIMESTAMP)   // @Column으로 잘 되면 지울 부분
//    @CreationTimestamp
//    private LocalDate schedule_start;

    @Column(nullable = false)
    private LocalDate schedule_start;

//    @Temporal(value = TemporalType.DATE)
//    private LocalDate schedule_end;

    @Column(nullable = false)
    private LocalDate schedule_end;

    @Column(nullable = false)
    private LocalTime schedule_start_time;

    @Column(nullable = false)
    private LocalTime schedule_end_time;

    @Column(nullable = false)
    private String memo;

//    // 생성된 시간, 수정된 시간 : 여기다 넣는 것이 맞는지는 잘 모르겠음
//    @Column(nullable = false)
//    private LocalTime created_at;
//
//    @Column(nullable = false)
//    private LocalTime modify_at;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy="schedule")
    public List<MemberSchedule> members = new ArrayList<>();

    @Builder
    public Schedule(String schedule_name, LocalDate schedule_start
            , LocalDate schedule_end, LocalTime schedule_start_time
            , LocalTime schedule_end_time, Project project, String memo) {
        this.schedule_name = schedule_name;
        this.schedule_start = schedule_start;
        this.schedule_end = schedule_end;
        this.schedule_start_time = schedule_start_time;
        this.schedule_end_time = schedule_end_time;
        this.project = project;
        this.memo = memo;
//        // 생성된 시간, 수정된 시간 : 다시 확인해볼것
//        this.created_at = created_at;
//        this.modify_at = modify_at;
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
//        // 생성된 시간, 수정된 시간 : 다시 확인해볼것
//        this.created_at = created_at;
//        this.modify_at = modify_at;

        return this;
    }
}

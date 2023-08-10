package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleStart(LocalDate schedule_start);
}

package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}

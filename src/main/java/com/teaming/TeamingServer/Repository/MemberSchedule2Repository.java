package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.MemberSchedule;
import com.teaming.TeamingServer.Domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface MemberSchedule2Repository extends JpaRepository<MemberSchedule, Long>{
    Optional<MemberSchedule> findById(Long scheduleId);

//    Optional<MemberSchedule> haveSchedules = memberScheduleRepository.findBySchedule(schedule);
}

package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.MemberSchedule;
import com.teaming.TeamingServer.Domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberScheduleRepository extends JpaRepository<MemberSchedule, Long> {

//    List<MemberSchedule> memberSchedules = memberScheduleRepository.findById(memberId).stream().toList();

    List<MemberSchedule> findByMember(Member member);

    Optional<MemberSchedule> findBySchedule(Schedule schedule);

//    Optional<MemberSchedule> haveSchedules = memberScheduleRepository.findBySchedule(schedule);

}

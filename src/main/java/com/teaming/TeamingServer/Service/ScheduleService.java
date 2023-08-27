package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.entity.*;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.MemberScheduleRepository;
import com.teaming.TeamingServer.Repository.ScheduleRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberScheduleRepository memberScheduleRepository;

    // 스케쥴 생성
    public ScheduleCreateResponseDto generateSchedule(Long memberId, Long projectId, ScheduleEnrollRequestDto scheduleEnrollRequestDto) {

        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_MODIFIED.value(), "Member not found"));

        Schedule schedule = Schedule.builder()
                .schedule_name(scheduleEnrollRequestDto.getSchedule_name())   // 스케줄 이름 설정
                .schedule_start(scheduleEnrollRequestDto.getSchedule_start())   // 스케줄시작날짜
                .schedule_start_time(scheduleEnrollRequestDto.getSchedule_start_time())    // 스케줄시작시간
                .schedule_end(scheduleEnrollRequestDto.getSchedule_end())    // 스케줄끝날짜
                .schedule_end_time(scheduleEnrollRequestDto.getSchedule_end_time())   // 스케줄끝시간
                .project(project)  // 프로젝트와 스케줄 연결
                .build();

        scheduleRepository.save(schedule);

        // Member 와 Schedule로 MemberSchedule 객체 생성 및 저장
        MemberSchedule memberSchedule = new MemberSchedule();
        memberSchedule.setMember(member);
        memberSchedule.setSchedule(schedule);
        memberScheduleRepository.save(memberSchedule);

        return ScheduleCreateResponseDto.builder()
                .ScheduleId(schedule.getSchedule_id())
                .build();

    }

    //스케쥴 삭제
    @Transactional
    public void deleteSchedule(Long memberId, Long projectId, Long scheduleId) {
        // 스케줄을 삭제하기 전에 해당 스케줄이 속한 프로젝트와 프로젝트에 해당하는 스케줄인지, 어떤 멤버가 연관되어있는지 확인해야 한다.
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Schedule not found"));
        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new EntityNotFoundException("Member not found"));
        // 프로젝트와 스케줄, 멤버가 연관되어 있는지 확인한다.

        if (!schedule.getProject().equals(project)) {
            throw new IllegalArgumentException("Schedule does not belong to the specified project.");
        }
        // 스케줄을 삭제한다.
        scheduleRepository.deleteById(scheduleId);
    }

    // 날짜별 스케쥴
    public List<FilteredSchedules> findSchedules(Long memberId, FilteringScheduleRequestDto filteringScheduleRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_MODIFIED.value(), "Member not found"));

        LocalDate targetDate = filteringScheduleRequestDto.getSchedule_start();

        List<Schedule> schedules = member.getMemberSchedules().stream()
                .map(MemberSchedule::getSchedule)
                .filter(schedule -> isDateWithinRange(targetDate, schedule.getSchedule_start(), schedule.getSchedule_end()))
                .collect(Collectors.toList());

        return schedules.stream()
                .map(this::mapToFilteredSchedules)
                .collect(Collectors.toList());
    }

    private boolean isDateWithinRange(LocalDate targetDate, LocalDate startDate, LocalDate endDate) {
        return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate);
    }

    // Schedule을 FilteredSchedules로 매핑
    private FilteredSchedules mapToFilteredSchedules(Schedule schedule) {
        return FilteredSchedules.builder()
                .schedule_name(schedule.getSchedule_name())
                .schedule_start(schedule.getSchedule_start())
                .schedule_start_time(schedule.getSchedule_start_time())
                .schedule_end(schedule.getSchedule_end())
                .schedule_end_time(schedule.getSchedule_end_time())
                .project_color(schedule.getProject().getProject_color())
                .build();
    }

    // 월별 날짜 리스트 조회

    public List<MonthlyResponseDto> getDateList(Long memberId, MonthlyRequestDto monthlyRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));

        Set<LocalDate> uniqueDatesInRequestedMonth = member.getMemberSchedules().stream()
                .map(MemberSchedule::getSchedule)
                .flatMap(schedule -> getDateRange(schedule.getSchedule_start(), schedule.getSchedule_end()))
                .filter(date -> isDateInRequestMonth(date, monthlyRequestDto.getDate_request()))
                .collect(Collectors.toSet()); // 중복을 제거하기 위해 Set으로 수집

        List<MonthlyResponseDto> monthlyResponseDtos = uniqueDatesInRequestedMonth.stream()
                .sorted()
                .map(date -> MonthlyResponseDto.builder().date_list(date).build())
                .collect(Collectors.toList());

        return monthlyResponseDtos;
    }

    private boolean isDateInRequestMonth(LocalDate dateToCheck, LocalDate requestedMonth) {
        return dateToCheck.getMonthValue() == requestedMonth.getMonthValue()
                && dateToCheck.getYear() == requestedMonth.getYear();
    }

    private Stream<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1)); // 끝 날짜도 포함시키기 위해 plusDays(1) 사용
    }
}


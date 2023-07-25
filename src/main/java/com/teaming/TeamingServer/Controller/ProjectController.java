package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.ScheduleDto;
import com.teaming.TeamingServer.Service.ScheduleService;
import com.teaming.TeamingServer.Service.ProjectService;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ScheduleService scheduleService;
    private final ProjectService projectService;

    // 스케줄 추가
    @PostMapping("/schedule")  // 주소랑 별개로 projectId와 memberId가 필요할까..?
    public ResponseEntity<BaseResponse> makeSchedule(
            @RequestBody ScheduleDto scheduleDto,
            @PathVariable("projectId") Long projectId,
            @PathVariable("memberId") Long memberId) {
        scheduleService.generateSchedule(projectId, memberId, scheduleDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(), "스케줄 추가 완료", null));
    }

    // 프로젝트의 스케줄 확인
    @GetMapping("/{projectId}/schedule")
    public ResponseEntity<BaseResponse<List<ScheduleDto>>> searchSchedules(@PathVariable("projectId") Long projectId) {
        List<ScheduleDto> list = projectService.searchSchedule(projectId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(), list));
    }

    // 프로젝트의 스케줄 삭제
    @DeleteMapping("/{projectId}/{scheduleId}")
    public ResponseEntity<BaseResponse> deleteSchedule (@PathVariable("projectId") Long projectId,
                                                        @PathVariable("scheduleId") Long scheduleId) {
        scheduleService.deleteSchedule(projectId, scheduleId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "스케줄 삭제 성공", null));
    }
}

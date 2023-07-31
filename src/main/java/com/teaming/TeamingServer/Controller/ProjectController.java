package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.ScheduleEnrollRequestDto;
import com.teaming.TeamingServer.Domain.Dto.ScheduleResponseDto;
import com.teaming.TeamingServer.Service.ScheduleService;
import com.teaming.TeamingServer.Service.ProjectService;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Service.FileService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ScheduleService scheduleService;
    private final ProjectService projectService;
    private final FileService fileService;

    // 스케줄 추가
    @PostMapping("/{projectId}/schedule/{memberId}")
    public ResponseEntity<BaseResponse> makeSchedule(
            @RequestBody ScheduleEnrollRequestDto scheduleEnrollRequestDto,
            @PathVariable("projectId") Long projectId,
            @PathVariable("memberId") Long memberId) {
        try {
            scheduleService.generateSchedule(projectId, memberId, scheduleEnrollRequestDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "일정이 추가되었습니다.:)", null));
        }
        catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());

            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    // 프로젝트의 스케줄 확인
    @GetMapping("/{projectId}/schedule")
    public ResponseEntity<BaseResponse<List<ScheduleResponseDto>>> searchSchedules(@PathVariable("projectId") Long projectId) {
        try {
            List<ScheduleResponseDto> list = projectService.searchSchedule(projectId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "프로젝트의 스케줄", list));
        }
        catch(BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());

            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    // 프로젝트의 스케줄 삭제
    @DeleteMapping("/{projectId}/{scheduleId}")
    public ResponseEntity<BaseResponse> deleteSchedule (@PathVariable("projectId") Long projectId,
                                                        @PathVariable("scheduleId") Long scheduleId) {
        try {
            scheduleService.deleteSchedule(projectId, scheduleId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "스케줄 삭제 성공", null));
        }
        catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());

            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    // 파일 업로드
    @PostMapping("/{projectId}/files-upload/{memberId}")
    public ResponseEntity<BaseResponse> uploadFile(@PathVariable Long projectId,
                                                   @PathVariable Long memberId,
                                                   @RequestPart MultipartFile file) {
        try {
            fileService.generateFile(projectId, memberId, file,false);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "파일을 업로드하였습니다", null));
        } catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());
            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    // 파일 삭제
    @DeleteMapping("/{projectId}/files/{fileId}")
    public ResponseEntity<BaseResponse> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "파일을 삭제하였습니다", null));
        } catch (BaseException e) {
            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    // 최종 파일 업로드
    @PostMapping("/{projectId}/final-file/{memberId}")
    public ResponseEntity<BaseResponse> uploadFinalFile(@PathVariable Long projectId,
                                                        @PathVariable Long memberId,
                                                        @RequestPart MultipartFile file) {
        try {
            fileService.generateFile(projectId, memberId, file,true);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "최종 파일을 업로드하였습니다", null));
        } catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());
            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }
}
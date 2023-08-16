package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.TestDto;
import com.teaming.TeamingServer.Service.AwsS3Service;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Service.MemberService;
import com.teaming.TeamingServer.Service.ScheduleService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함 - ResponseBody 어노테이션도 포함하고 있음
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AwsS3Service awsS3Service;
    private final ScheduleService scheduleService;

    @PostMapping("/member/{memberId}/change-password/check-password")
    public ResponseEntity checkCurrentPassword(@PathVariable("memberId") Long memberId
                                            , @RequestBody CheckCurrentPasswordRequestDto checkCurrentPasswordRequestDto) {

        return memberService.checkCurrentPassword(memberId, checkCurrentPasswordRequestDto);

    }

    @PostMapping("/member/{memberId}/change-password")
    public ResponseEntity changePassword(@PathVariable("memberId") Long memberId
                                        , @RequestBody MemberChangePasswordRequestDto memberChangePasswordRequestDto) {
        return memberService.changePassword(memberId, memberChangePasswordRequestDto);
    }

    @GetMapping("/member/{memberId}/mypage")
    public ResponseEntity myPage(@PathVariable("memberId") Long memberId) {
        return memberService.MemberMyPage(memberId);
    }

    // 프로필 이미지 변경
    @PatchMapping("/member/{memberId}/mypage/change-image")
    public ResponseEntity changeProfileImage(@RequestPart("change_image_file") MultipartFile multipartFile, @PathVariable Long memberId) throws IOException {
        return awsS3Service.profileImageUpload(multipartFile, "image/", memberId);
    }

    @PatchMapping("/member/{memberId}/mypage/change-nickname")
    public ResponseEntity changeNickName(@PathVariable("memberId") Long memberId
                                         , @RequestBody MemberNicknameChangeRequestDto memberNicknameChangeRequestDto) {
        return memberService.changeNickName(memberId, memberNicknameChangeRequestDto);
    }

    @GetMapping("/member/{memberId}/home")
    public ResponseEntity mainPage(@PathVariable("memberId") Long memberId) {
        return memberService.mainPage(memberId);
    }

    @GetMapping("/member/{memberId}/portfolio")
    public ResponseEntity portfolioPage(@PathVariable("memberId") Long memberId) {
        return memberService.portfolioPage(memberId);
    }

    @PostMapping("/member/saveData")
    public ResponseEntity saveData(@RequestBody TestDto testDto) {
        memberService.saveMemberProject(testDto.getMember_id(), testDto.getProject_id(), testDto.getSchedule_id());
        return ResponseEntity.ok("성공");
    }


    // 프로젝트의 날짜별 스케줄 확인
    @PostMapping ("/member/{memberId}/schedules/{schedule_start}")
    public ResponseEntity<BaseResponse<List<FilteredSchedules>>> scheduleByDate(
            @PathVariable("memberId") Long memberId,
            @PathVariable("schedule_start") LocalDate schedule_start) {
        try {

            List<FilteredSchedules> filteredSchedules = scheduleService.findSchedules(memberId,schedule_start);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "사용자의 일정", filteredSchedules));
        }
        catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());
            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }
}
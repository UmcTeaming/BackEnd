package com.teaming.TeamingServer.Controller;


import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Service.MemberService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller            // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public BaseResponse signup(@RequestBody MemberRequestDto memberRequestDto) {
        memberService.join(memberRequestDto);
        // 여기서 Exception 을 잡아야 하나?
        return new BaseResponse(200, "회원가입이 완료되었습니다.");
    }

    // 이메일 중복체크
    @GetMapping("/signup/email-duplication")
    public BaseResponse duplicateEmail(@RequestBody MemberRequestDto memberRequestDto) {
        try {
            memberService.validateDuplicateMember(memberRequestDto.getEmail());
        } catch (IllegalArgumentException e) {
            return new BaseResponse<>(400, e.getMessage());
        }
        return new BaseResponse<>(200, "사용가능한 이메일입니다.");
    }

}

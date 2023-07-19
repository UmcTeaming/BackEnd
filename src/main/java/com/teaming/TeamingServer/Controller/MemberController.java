package com.teaming.TeamingServer.Controller;


import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller            // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

//    @GetMapping("/")
//    public String Home() {
//        return "home";
//    }

//    @GetMapping("/member/signup")
//    public String signupMemberForm() {
//        return "member/signupMemberForm";
//    }

    @PostMapping("/member/signup")
    public ResponseEntity createMember(@RequestBody Member member) {
        // 여기서 UserData는 프론트엔드에서 보낸 JSON 데이터를 자바 객체로 변환한 클래스를 가정합니다.

        // 회원가입 로직을 수행합니다.
        // 이 곳에서 데이터베이스에 저장 등의 처리를 수행합니다.
        // ...

        // 회원가입이 성공했을 때
        return ResponseEntity.ok().body("회원가입이 성공적으로 완료되었습니다.");
    }
}

package com.teaming.TeamingServer.Service;

import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity changePassword(); // PasswordChange Request 매개변수로 들어갈 예정

    ResponseEntity checkCurrentPassword(); // 비밀번호 변경을 위한 현재 비밀번호 체크 : CheckCurrentPasswordRequest 가 매개변수로 들어갈 예정

    ResponseEntity MemberMyPage(); // 액세스 토큰을 확인한 후, 사용자 정보 페이지 반환 : MemberMyPageRequest 가 매개변수로 들어갈 예정

}

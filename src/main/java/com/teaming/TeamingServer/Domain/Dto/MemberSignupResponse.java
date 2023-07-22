package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupResponse {
    BaseResponse emailDuplicationResponse;
}

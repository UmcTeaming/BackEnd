package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.Dto.mainPageDto.InviteMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectInviteResponseDto {
    private List<InviteMember> members;
}

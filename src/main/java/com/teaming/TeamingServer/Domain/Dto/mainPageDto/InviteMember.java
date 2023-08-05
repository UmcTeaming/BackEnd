package com.teaming.TeamingServer.Domain.Dto.mainPageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteMember {
    private String member_name;
    private String member_image;
    private String member_email;
}

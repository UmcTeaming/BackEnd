package com.teaming.TeamingServer.Domain.Dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String writer;
    private String content;
    private LocalDateTime create_at;
    private String profile_image;
}

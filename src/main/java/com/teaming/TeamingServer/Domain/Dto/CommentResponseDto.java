package com.teaming.TeamingServer.Domain.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private String writer;
    private String content;
    private LocalDateTime create_at;
    private String profile_image;
}

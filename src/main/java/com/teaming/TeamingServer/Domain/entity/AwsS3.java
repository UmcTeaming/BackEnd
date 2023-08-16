package com.teaming.TeamingServer.Domain.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwsS3 {
    private String key;
    private String path;
}

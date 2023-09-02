package com.teaming.TeamingServer.Domain.Dto.response;


import com.teaming.TeamingServer.Domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileDetailResponseDto  {

    private String file_type;
    private String file_name;
    private String file; // 파일에 대한 url 반환
    private int comment;
    private Status project_status;
    private Long file_id;

}

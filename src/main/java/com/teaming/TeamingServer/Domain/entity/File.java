package com.teaming.TeamingServer.Domain.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class File extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long file_id;

    @Column(nullable = true)
    private Boolean file_status;

    @Column(nullable = false)
    private String fileName; // 파일 이름 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 파일을 올린 멤버와의 관계

    @Column(nullable = false)
    private String file_type;

   @Column(nullable = false)
   private String fileUrl;



    @Builder
    public File(String fileName, String file_type,Project project, Member member, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.file_type = file_type;
        this.project = project;
        this.member = member;
    }


}

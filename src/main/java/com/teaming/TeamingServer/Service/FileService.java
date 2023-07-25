package com.teaming.TeamingServer.Service;


import com.teaming.TeamingServer.Domain.Dto.CommentResponseDto;
import com.teaming.TeamingServer.Domain.entity.Comment;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.Project;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.CommentRepository;
import com.teaming.TeamingServer.Repository.FileRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {


     private final FileRepository fileRepository;
     private final ProjectRepository projectRepository;
     private final MemberRepository memberRepository;

    public List<CommentResponseDto> searchComment(Long fileId) {


        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found with id: " + fileId));

        // 파일에 해당하는 코멘트들을 조회합니다.


        // 조회한 코멘트들을 CommentResponseDto 형태로 변환하여 리스트에 담습니다.
        List<CommentResponseDto> result = file.getComments().stream()
                .map(comment -> new CommentResponseDto(comment.getWriter(), comment.getContent(), comment.getCreatedAt()))
                .collect(Collectors.toList());
        return result;
    }

    private String uploadDir = "C:\\Users\\82103\\Desktop\\UMC\\";

    public void generateFile(Long projectId, Long memberId, MultipartFile file) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 ID"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 회원 ID"));

        // 파일 정보 저장
        String sourceFileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(sourceFileName)) {
            throw new BaseException(400, "업로드된 파일의 이름이 유효하지 않습니다");
        }

        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        FilenameUtils.removeExtension(sourceFileName);

        String fileUrl = "C:\\Users\\82103\\Desktop\\UMC\\";

        File newFile = File.builder()
                .fileName(sourceFileName)
                .file_type(sourceFileNameExtension)
                .fileUrl(fileUrl)
                .project(project)
                .member(member)
                .build();

        // 파일 엔티티 저장
        project.getFiles().add(newFile);
        projectRepository.save(project);

        // 파일 저장
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BaseException(500, "파일을 저장하는데 실패하였습니다");
        }
    }

    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found with id: " + fileId));

        // 파일 삭제
        fileRepository.delete(file);
    }
}
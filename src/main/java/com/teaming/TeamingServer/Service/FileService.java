package com.teaming.TeamingServer.Service;


import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.Project;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.FileRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.teaming.TeamingServer.Service.FileStore.fileDir;


@Service
@Transactional
@RequiredArgsConstructor
public class FileService {


    private final FileRepository fileRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @Value("${file.upload-dir}")
    public String fileDir;

    // 코멘트 찾기
    public List<CommentResponseDto> searchComment(Long fileId) {

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found with id: " + fileId));

        // 파일에 해당하는 코멘트들을 조회합니다.
        List<CommentResponseDto> result = file.getComments().stream()
                .map(comment -> new CommentResponseDto(comment.getWriter(), comment.getContent(), comment.getCreatedAt(), comment.getMember().getProfile_image()))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    //파일 업로드

//    private String uploadDir = "/Users/onam-ui/Desktop/Projects/TeamingFile/"; - 남의 경로

    public void generateFile(Long projectId, Long memberId, MultipartFile file, Boolean fileStatus) {
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

        String fileUrl = fileDir;

        File newFile = File.builder()
                .fileName(sourceFileName)
                .file_type(sourceFileNameExtension)
                .fileUrl(fileUrl)
                .project(project)
                .member(member)
                .file_status(fileStatus) // file_status 설정
                .build();

        // 파일 엔티티 저장
        project.getFiles().add(newFile);
        projectRepository.save(project);

        // 파일 저장
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = Paths.get(fileDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(500, "파일을 저장하는데 실패하였습니다");
        }
    }


    //파일 삭제
    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found with id: " + fileId));

        fileRepository.delete(file); // 파일 엔티티를 데이터베이스에서 삭제
        // 파일 삭제
    }

    // 프로젝트 파일 조회
    public List<FileListResponseDto> searchFile(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 ID"));

        Map<LocalDate, List<FileDetailResponseDto>> fileInfoByDate = new HashMap<>();

        project.getFiles().stream()
                .filter(file -> !file.getFile_status()) // file_status가 false인 파일들만 고려
                .forEach(file -> {
                    int commentCount = file.getComments().size();
                    FileDetailResponseDto fileDetailResponseDto = new FileDetailResponseDto(
                            file.getFile_type(),
                            file.getFileName(),
                            file.getFileUrl(),
                            commentCount
                    );

                    LocalDateTime createdAt = file.getCreatedAt();
                    LocalDate date = createdAt.toLocalDate();

                    List<FileDetailResponseDto> filesbyDate = fileInfoByDate.getOrDefault(date, new ArrayList<>());
                    filesbyDate.add(fileDetailResponseDto);
                    fileInfoByDate.put(date, filesbyDate);

                });

        if (fileInfoByDate.isEmpty()) {
            return null;
        }

        return fileInfoByDate.entrySet().stream()
                .map(entry -> new FileListResponseDto(entry.getKey().atStartOfDay(), entry.getValue()))
                .collect(Collectors.toList());

    }

    // 하나의 파일에 대한 정보 조회

    public SingleFileResponseDto searchOneFile(Long memberId, Long projectId, Long fileId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 ID"));

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 파일 ID"));

        SingleFileResponseDto information = new SingleFileResponseDto(
                file.getFile_type(),
                file.getFileName(),
                file.getMember().getName(),
                file.getCreatedAt().toLocalDate()
        );

        return information;

    }

    // 프로젝트 최종 파일 조회
    public List<FileListResponseDto> searchFinalFile(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 ID"));

        Map<LocalDate, List<FileDetailResponseDto>> fileInfoByDate = new HashMap<>();

        project.getFiles().stream()
                .filter(file -> file.getFile_status()) // file_status가 true인 파일들만 고려
                .forEach(file -> {
                    int commentCount = file.getComments().size();
                    FileDetailResponseDto fileDetailResponseDto = new FileDetailResponseDto(
                            file.getFile_type(),
                            file.getFileName(),
                            file.getFileUrl(),
                            commentCount
                    );

                    LocalDateTime createdAt = file.getCreatedAt();
                    LocalDate date = createdAt.toLocalDate();

                    List<FileDetailResponseDto> filesbyDate = fileInfoByDate.getOrDefault(date, new ArrayList<>());
                    filesbyDate.add(fileDetailResponseDto);
                    fileInfoByDate.put(date, filesbyDate);

//
                });

        if (fileInfoByDate.isEmpty()) {
            return null;
        }

        return fileInfoByDate.entrySet().stream()
                .map(entry -> new FileListResponseDto(entry.getKey().atStartOfDay(), entry.getValue()))
                .collect(Collectors.toList());

    }

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public class DocumentService {
        public ViewLinkResponseDto convertFileToViewLink(String fileLink) {
            return null;
        }
    }
}

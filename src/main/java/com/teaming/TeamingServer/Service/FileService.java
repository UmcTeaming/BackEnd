package com.teaming.TeamingServer.Service;


import com.teaming.TeamingServer.Domain.Dto.response.*;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.Project;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.FileRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
    public List<CommentResponseDto> searchComment(Long memberId, Long fileId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found"));

        // 파일에 해당하는 코멘트들을 조회합니다.
        List<CommentResponseDto> result = file.getComments().stream()
                .map(comment -> new CommentResponseDto(comment.getComment_id(),comment.getMember().getName(), comment.getContent(), comment.getCreatedAt(), comment.getMember().getProfile_image()))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    //파일 업로드
    public FileUploadResponseDto generateFile(Long projectId, Long memberId, MultipartFile file, Boolean fileStatus) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "Project not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(404, "Member not found"));


        // 파일 정보 저장
       // String sourceFileName = "projectId" + projectId + "-" +file.getOriginalFilename();
        String sourceFileName = "projectId" + projectId + "-" + file.getOriginalFilename();
        if (StringUtils.isEmpty(sourceFileName)) {
            throw new BaseException(400, "File name is not valid");
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

        fileRepository.save(newFile);

        // 파일 저장
        try {
            //String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileName = StringUtils.cleanPath(sourceFileName);
            Path filePath = Paths.get(fileDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(500, "Fail to save file");
        }

        FileUploadResponseDto fileUploadResponseDto = new FileUploadResponseDto(newFile.getFile_id());

        return fileUploadResponseDto;
    }


    //파일 삭제
    public void deleteFile(Long projectId, Long memberId, Long fileId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));

        fileRepository.delete(file); // 파일 엔티티를 데이터베이스에서 삭제
        // 파일 삭제
    }

    // 프로젝트 파일 조회
    public List<FileListResponseDto> searchFile(Long memberId, Long projectId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(404, "Member not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "Project not found"));

        Map<LocalDate, List<FileDetailResponseDto>> fileInfoByDate = new HashMap<>();

        project.getFiles().stream()
                .filter(file -> !file.getFile_status()) // file_status가 false인 파일들만 고려
                .forEach(file -> {
                    int commentCount = file.getComments().size();

                    String fileName = file.getFileName();
                    int hyphenIndex = fileName.indexOf("-");
                    if (hyphenIndex != -1) {
                        fileName = fileName.substring(hyphenIndex + 1);
                    }

                    FileDetailResponseDto fileDetailResponseDto = new FileDetailResponseDto(
                            file.getFile_type(),
                            fileName,
                            file.getFileUrl(),
                            commentCount,
                            file.getFile_id()
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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(404, "Member not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "Project not found"));

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(404, "File not found"));

        String fileName = file.getFileName();
        int hyphenIndex = fileName.indexOf("-");
        if (hyphenIndex != -1) {
            fileName = fileName.substring(hyphenIndex + 1);
        }

        SingleFileResponseDto information = new SingleFileResponseDto(
                file.getProject().getProject_name(),
                file.getFile_type(),
                fileName,
                file.getMember().getName(),
                file.getCreatedAt().toLocalDate()
        );

        return information;

    }

    // 프로젝트 최종 파일 조회
    public List<FileListResponseDto> searchFinalFile(Long memberId, Long projectId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(404, "Member not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "Project not found"));

        Map<LocalDate, List<FileDetailResponseDto>> fileInfoByDate = new HashMap<>();

        project.getFiles().stream()
                .filter(file -> file.getFile_status()) // file_status가 true인 파일들만 고려
                .forEach(file -> {
                    int commentCount = file.getComments().size();
                    String fileName = file.getFileName();
                    int hyphenIndex = fileName.indexOf("-");
                    if (hyphenIndex != -1) {
                        fileName = fileName.substring(hyphenIndex + 1);
                    }
                    FileDetailResponseDto fileDetailResponseDto = new FileDetailResponseDto(
                            file.getFile_type(),
                            fileName,
                            file.getFileUrl(),
                            commentCount,
                            file.getFile_id()
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
}



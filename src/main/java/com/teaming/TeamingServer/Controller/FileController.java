package com.teaming.TeamingServer.Controller;

import com.amazonaws.services.s3.model.S3Object;
import com.teaming.TeamingServer.Domain.Dto.request.CommentEnrollRequestDto;
import com.teaming.TeamingServer.Domain.Dto.response.CommentEnrollResponseDto;
import com.teaming.TeamingServer.Domain.Dto.response.CommentResponseDto;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.FileRepository;
import com.teaming.TeamingServer.Service.AwsS3Service;
import com.teaming.TeamingServer.Service.CommentService;
import com.teaming.TeamingServer.Service.FileService;
import com.teaming.TeamingServer.Service.ProjectService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {


    private final CommentService commentService;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final AwsS3Service awsS3Service;
    private final ProjectService projectService;


    //코멘트 생성
    @PostMapping("/{memberId}/{fileId}/comments")
    public ResponseEntity<BaseResponse<CommentEnrollResponseDto>> makeComment(
            @RequestBody CommentEnrollRequestDto commentEnrollRequestDto,
            @PathVariable("fileId") Long fileId,
            @PathVariable("memberId") Long memberId) {
        try {

         CommentEnrollResponseDto commentEnrollResponseDto =  commentService.generateComment(fileId, memberId, commentEnrollRequestDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "댓글을 등록하였습니다", commentEnrollResponseDto));
        } catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());

            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }


    //코멘트 조회
    @GetMapping("/{memberId}/{fileId}/comments")
    public ResponseEntity<BaseResponse<List<CommentResponseDto>>> searchComments(@PathVariable("fileId") Long fileId,
                                                                                 @PathVariable("memberId") Long memberId) {
        try {

            List<CommentResponseDto> list = fileService.searchComment(memberId, fileId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "댓글 정보를 불러왔습니다", list));
        } catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());

            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }


    //코멘트 삭제
    @DeleteMapping("/{memberId}/{fileId}/comments/{commentId}")
    public ResponseEntity<BaseResponse> deleteComment(@PathVariable("fileId") Long fileId, @PathVariable("commentId") Long commentId,
                                                      @PathVariable("memberId") Long memberId) {
        try {
            commentService.deleteComment(memberId, fileId, commentId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "댓글을 삭제했습니다", null));
        } catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());

            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }


    // 파일 다운로드
    @GetMapping(value = "/{memberId}/{projectId}/files/{fileId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"
                , "https://teaming-six.vercel.app/", "http://teaming-six.vercel.app/"}
                , exposedHeaders = "Content-Disposition")
    public ResponseEntity download(@PathVariable("fileId") Long fileId) throws MalformedURLException {

        File file = fileRepository.findById(fileId).orElseThrow(
                () -> new BaseException(404, "유효하지 않은 파일 ID")
        );

        byte[] data = awsS3Service.download(file);

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{memberId}/{projectId}/files/{fileId}/view")
    public ResponseEntity fileViewer(@PathVariable("fileId") Long fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(
                () -> new BaseException(404, "유효하지 않은 파일 ID")
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "파일 링크입니다.", file.getFileUrl()));
    }
}

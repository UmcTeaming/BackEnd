package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.CommentEnrollRequestDto;
import com.teaming.TeamingServer.Domain.Dto.CommentResponseDto;
import com.teaming.TeamingServer.Domain.Dto.FileLinkRequestDto;
import com.teaming.TeamingServer.Domain.Dto.ViewLinkResponseDto;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Service.CommentService;
import com.teaming.TeamingServer.Service.FileService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {


    private final CommentService commentService;
    private final FileService fileService;


    //코멘트 생성
    @PostMapping("/{memberId}/{fileId}/comments")
    public ResponseEntity<BaseResponse> makeComment(
            @RequestBody CommentEnrollRequestDto commentEnrollRequestDto,
            @PathVariable("fileId") Long fileId,
            @PathVariable("memberId") Long memberId) {
        try {
            commentService.generateComment(fileId, memberId, commentEnrollRequestDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "답글을 등록하였습니다", null));
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
            List<CommentResponseDto> list = fileService.searchComment(fileId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "코멘트 정보를 불러왔습니다", list));
        } catch (BaseException e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());
            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    //코멘트 삭제
    @DeleteMapping("/{memberId}/{fileId}/comments/{commentId}")
    public ResponseEntity<BaseResponse> deleteComment (@PathVariable("fileId") Long fileId, @PathVariable("commentId") Long commentId,
                                                       @PathVariable("memberId") Long memberId) {

        try {
            commentService.deleteComment(fileId, commentId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "커멘트를 삭제했습니다", null));
        }catch( BaseException e){
            BaseErrorResponse errorResponse = new BaseErrorResponse(e.getCode(), e.getMessage());
            return ResponseEntity
                    .status(e.getCode())
                    .body(new BaseResponse<>(e.getCode(), e.getMessage(), null));
        }
    }

    // 문서 뷰어 띄우기
    @PostMapping("/viewDocument")
    public ResponseEntity<?> viewDocument(@RequestBody FileLinkRequestDto request) {
        try {
            ViewLinkResponseDto response;
            FileService.DocumentService documentService = null; // 인스턴스화 방법이 여전히 필요합니다.
            response = documentService.convertFileToViewLink((String) request.getFileLink());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "문서 뷰어 로드 성공하였습니다.", response));
        } catch (Exception e) {
            BaseErrorResponse errorResponse = new BaseErrorResponse(404, "뷰어 로드 중 오류 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(404, "뷰어 로드 중 오류 발생하였습니다.", errorResponse));
        }
    }

}





package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.CommentEnrollRequestDto;
import com.teaming.TeamingServer.Domain.Dto.CommentResponseDto;
import com.teaming.TeamingServer.Service.CommentService;
import com.teaming.TeamingServer.Service.FileService;
import com.teaming.TeamingServer.common.BaseResponse;
import jakarta.validation.constraints.Min;
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


    @PostMapping("/{fileId}/{memberId}/comments")
    public ResponseEntity<BaseResponse> makeComment(
            @RequestBody CommentEnrollRequestDto commentEnrollRequestDto,
            @PathVariable("fileId") Long fileId,
            @PathVariable("memberId") Long memberId) {
        commentService.generateComment(fileId, memberId, commentEnrollRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(), "답글을 등록하였습니다", null));
    }

    @GetMapping("/{fileId}/comments")
    public ResponseEntity<BaseResponse<List<CommentResponseDto>>> searchComments(@PathVariable("fileId") Long fileId){
        List<CommentResponseDto> list = fileService.searchComment(fileId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse<>(HttpStatus.OK.value(),"코멘트 정보를 불러왔습니다",list));
    }

    @DeleteMapping("/{fileId}/comments/{commentId}")
    public ResponseEntity<BaseResponse> deleteComment (@PathVariable("fileId") Long fileId, @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(fileId, commentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "커멘트를 삭제했습니다", null));
    }

}

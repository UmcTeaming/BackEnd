package com.teaming.TeamingServer.Service;


import com.fasterxml.jackson.databind.ser.Serializers;
import com.teaming.TeamingServer.Domain.Dto.CommentEnrollRequestDto;
import com.teaming.TeamingServer.Domain.Dto.CommentEnrollResponseDto;
import com.teaming.TeamingServer.Domain.Dto.CommentResponseDto;
import com.teaming.TeamingServer.Domain.entity.Comment;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.CommentRepository;
import com.teaming.TeamingServer.Repository.FileRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final MemberRepository memberRepository;

    // comment 생성하기
    public CommentEnrollResponseDto generateComment(Long fileId, Long memberId, CommentEnrollRequestDto commentEnrollRequestDto) {
        // 요청으로부터 댓글 내용을 가져옵니다.
        String content = commentEnrollRequestDto.getContent();

        // 파일과 멤버를 데이터베이스에서 조회합니다.
        File file = fileRepository.findById(fileId).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found"));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));


        Comment comment = Comment.builder()
                .writer(member.getName()) // 댓글 작성자를 멤버의 이름으로 설정
                .content(content) // 댓글 내용 설정
                .file(file) // 댓글과 파일 연결
                .member(member) // 댓글과 멤버 연결
                .build();


        // 댓글을 데이터베이스에 저장합니다.
        commentRepository.save(comment);

        CommentEnrollResponseDto commentEnrollResponseDto = new CommentEnrollResponseDto(comment.getComment_id());
        return commentEnrollResponseDto;
    }


    //comment 삭제하기
    @Transactional
    public void deleteComment(Long memberId,Long fileId, Long commentId) {
        // 댓글을 삭제하기 전에 해당 댓글이 속한 파일과 파일에 해당하는 댓글인지 확인해야 합니다.
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));
        File file = fileRepository.findById(fileId).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "File not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(),  "Comment not found"));
        // 파일과 댓글이 연관되어 있는지 확인합니다.

        if (!comment.getFile().equals(file)) {
            throw new IllegalArgumentException("Comment does not belong to the specified file.");
        }
        // 댓글을 삭제합니다.
        commentRepository.deleteById(commentId);
    }
    }



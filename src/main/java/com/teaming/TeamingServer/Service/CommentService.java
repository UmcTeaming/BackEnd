package com.teaming.TeamingServer.Service;


import com.teaming.TeamingServer.Domain.Dto.CommentEnrollRequestDto;
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

    public void generateComment(Long fileId, Long memberId, CommentEnrollRequestDto commentEnrollRequestDto) {
        // 요청으로부터 댓글 내용을 가져옵니다.
        String content = commentEnrollRequestDto.getContent();

        // 파일과 멤버를 데이터베이스에서 조회합니다.
        File file = fileRepository.findById(fileId).orElseThrow(() -> new EntityNotFoundException("File not found with id: " + fileId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));


        Comment comment = Comment.builder()
                .writer(member.getName()) // 댓글 작성자를 멤버의 이름으로 설정
                .content(content) // 댓글 내용 설정
                .file(file) // 댓글과 파일 연결
                .member(member) // 댓글과 멤버 연결
                .build();

        // 댓글을 데이터베이스에 저장합니다.
        commentRepository.save(comment);
    }



}

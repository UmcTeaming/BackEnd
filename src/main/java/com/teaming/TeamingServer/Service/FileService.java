package com.teaming.TeamingServer.Service;


import com.teaming.TeamingServer.Domain.Dto.CommentResponseDto;
import com.teaming.TeamingServer.Domain.entity.Comment;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.CommentRepository;
import com.teaming.TeamingServer.Repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {


     private final FileRepository fileRepository;

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
}

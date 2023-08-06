package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
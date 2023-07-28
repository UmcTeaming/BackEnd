package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Member;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
}

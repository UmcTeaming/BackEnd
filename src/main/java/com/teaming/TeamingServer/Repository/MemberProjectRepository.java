package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.MemberProject;
import com.teaming.TeamingServer.Domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    List<MemberProject> findByMember(Member member); // member 로 MemberProject 를 찾는 것

}

package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.MemberProject;
import com.teaming.TeamingServer.Domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    List<MemberProject> findByMember(Member member); // member 로 MemberProject 를 찾는 것

    List<MemberProject> findByProject(Project project); // project 로 참여 멤버들 조회

}

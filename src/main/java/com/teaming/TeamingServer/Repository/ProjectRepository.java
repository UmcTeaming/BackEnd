package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}

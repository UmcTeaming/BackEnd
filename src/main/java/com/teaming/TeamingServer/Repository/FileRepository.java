package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File,Long> {

    Optional<File> findById(Long fileId);
}

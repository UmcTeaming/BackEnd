package com.teaming.TeamingServer.Repository;

import com.teaming.TeamingServer.Domain.entity.File;
import org.aspectj.weaver.loadtime.Options;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {

}

package com.teaming.TeamingServer.Repository;

import org.springframework.data.repository.CrudRepository;

public interface TokenRedisRepository extends CrudRepository<String, String> {
}

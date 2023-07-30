package com.teaming.TeamingServer.Config.Redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RedisProperties {
//    @Value("${spring.redis.host}")
    private String host = "localhost";
//    @Value("${spring.redis.port}")
    private int port = 6379;

}

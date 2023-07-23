package com.teaming.TeamingServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeamingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamingServerApplication.class, args);
	}

}

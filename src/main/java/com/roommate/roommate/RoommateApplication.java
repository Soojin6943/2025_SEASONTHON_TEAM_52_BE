package com.roommate.roommate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoommateApplication {
	public static void main(String[] args) {
		var dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
				.ignoreIfMissing()
				.load();

		// .env의 모든 key/value를 System Property로 등록
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(RoommateApplication.class, args);
	}
}


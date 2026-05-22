package com.example.ratelimiter;

import com.example.ratelimiter.util.JwtUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RatelimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				RatelimiterApplication.class,
				args
		);
	}

	@Bean
	public CommandLineRunner testToken(
			JwtUtil jwtUtil
	) {

		return args -> {

			System.out.println(
					"JWT = " +
							jwtUtil.generateToken("101")
			);

		};
	}
}
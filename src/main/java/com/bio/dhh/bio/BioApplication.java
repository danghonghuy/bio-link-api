package com.bio.dhh.bio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BioApplication {

	public static void main(String[] args) {
		SpringApplication.run(BioApplication.class, args);
	}
	// ========== THÊM ĐOẠN CODE NÀY VÀO ==========
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("========== DEBUG ENVIRONMENT VARIABLES ==========");
			String dbHost = System.getenv("MYSQLHOST");
			String dbPort = System.getenv("MYSQLPORT");
			String dbName = System.getenv("MYSQLDATABASE");
			String dbUser = System.getenv("MYSQLUSER");
			String dbPass = System.getenv("MYSQLPASSWORD");

			System.out.println("[DEBUG] MYSQLHOST: " + dbHost);
			System.out.println("[DEBUG] MYSQLPORT: " + dbPort);
			System.out.println("[DEBUG] MYSQLDATABASE: " + dbName);
			System.out.println("[DEBUG] MYSQLUSER: " + dbUser);
			System.out.println("[DEBUG] MYSQLPASSWORD is set: " + (dbPass != null && !dbPass.isEmpty())); // Chỉ kiểm tra có tồn tại hay không, không in password
			System.out.println("=============================================");
		};
	}
	// =================================================
}

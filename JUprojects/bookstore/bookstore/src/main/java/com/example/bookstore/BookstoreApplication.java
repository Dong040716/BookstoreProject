package com.example.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// @SpringBootApplication 是 Spring Boot 的核心注解
// 它包含 @Configuration（配置类）、@EnableAutoConfiguration（自动配置） 和 @ComponentScan（包扫描）
// 作用：告诉 Spring Boot 从这个类开始启动，并扫描该包及子包下的所有组件
@SpringBootApplication
public class BookstoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}
}

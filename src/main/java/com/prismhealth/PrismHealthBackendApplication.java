package com.prismhealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.services.AuthService;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class PrismHealthBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrismHealthBackendApplication.class, args);
	}
	@Bean
	public RestTemplate getRestTemplate(){
		return  new RestTemplate();
	}
	@Bean
	public ExecutorService taskExecutor() {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		return executor;
	}
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
	@Bean
	public ObjectMapper getObjectMapper(){
		return new ObjectMapper();
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public OkHttpClient getOkHttpClient(){
		return new OkHttpClient();
	}

}

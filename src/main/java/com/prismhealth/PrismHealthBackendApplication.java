package com.prismhealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Positions;
import com.prismhealth.Models.Users;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.UserRolesRepo;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableSwagger2
@SpringBootApplication
@EnableAsync
public class PrismHealthBackendApplication implements ApplicationRunner {
	@Autowired
	private UserRolesRepo rolesRepo;
	@Autowired
	private AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(PrismHealthBackendApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ExecutorService taskExecutor() {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		return executor;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public OkHttpClient getOkHttpClient() {
		return new OkHttpClient();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// accountRepository.deleteAll();
		// rolesRepo.deleteAll();
		if (!accountRepository.existsByPhone("+254711111111")) {
			Users users1 = new Users();
			Positions positions = new Positions();
			positions.setLongitude(Double.parseDouble("24.345"));
			positions.setLatitude(Double.parseDouble("-1.2345"));
			positions.setLocationName("location");
			users1.setPassword(passwordEncoder().encode("password"));
			users1.setPhone("+254711111111");
			users1.setEmail("joshuajoe12561@gmail.com");
			users1.setFirstName("Admin");
			users1.setLocationName("Location");
			users1.setPosition(new double[] { Double.parseDouble("-1.2345"), Double.parseDouble("24.345") });
			users1.setPositions(positions);
			users1.setEmergencyContact1(null);
			users1.setEmergencyContact2(null);
			users1.setAccountType("ADMIN");

			users1.setVerified(true);
			users1.setBlocked(false);
			users1.setDeleted(false);

			users1 = accountRepository.save(users1);

			UserRoles role = new UserRoles();
			role.setAssignedBy("DEFAULT");
			role.setRole("ROLE_ADMIN");// "ROLE_ADMIN", "ROLE_HELP_SUPPORT", "ROLE_SITE_CONTENT_UPDATER"));
			role.setUserId(users1.getPhone());
			rolesRepo.save(role);
		}

	}


}

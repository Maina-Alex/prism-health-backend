package com.prismhealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Positions;
import com.prismhealth.Models.Users;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.repository.UserRepository;
import com.prismhealth.repository.UserRolesRepo;
import com.prismhealth.services.MailServiceImpl;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableSwagger2
@SpringBootApplication
@EnableAsync
@AllArgsConstructor
public class PrismHealthBackendApplication implements ApplicationRunner {
	private final UserRolesRepo rolesRepo;
	private final UserRepository userRepository;
	private final MailServiceImpl mailService;

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
		// userRepository.deleteAll();
		// rolesRepo.deleteAll();
		if (!userRepository.existsByPhone("+254711111111")) {
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

			users1 = userRepository.save(users1);

			UserRoles role = new UserRoles();
			role.setAssignedBy("DEFAULT");
			role.setRole("ROLE_ADMIN");// "ROLE_ADMIN", "ROLE_HELP_SUPPORT", "ROLE_SITE_CONTENT_UPDATER"));
			role.setUserId(users1.getPhone());
			rolesRepo.save(role);
		}

	}


}

package de.uniwue.dachs.haeuserbuch_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HaeuserbuchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaeuserbuchBackendApplication.class, args);
	}

}

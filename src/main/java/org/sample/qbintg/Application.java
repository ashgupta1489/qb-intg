package org.sample.qbintg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;


/**
 * Entry point to QuickBook Project
 * @author Ashish
 *
 */
@SpringBootApplication
@EnableJpaAuditing
public class Application extends RepositoryRestMvcConfiguration  {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}

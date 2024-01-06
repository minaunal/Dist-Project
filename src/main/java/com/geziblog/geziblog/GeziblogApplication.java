package com.geziblog.geziblog;

import com.geziblog.geziblog.controller.UserController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.geziblog.geziblog.controller.repository"})
@EntityScan(basePackages = {"com.geziblog.geziblog.entity"})
@SpringBootApplication(scanBasePackages = {"com.geziblog.geziblog"})
@EnableAutoConfiguration

public class GeziblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeziblogApplication.class, args);
	}

}

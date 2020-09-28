package cmod.repz.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "cmod.repz.application.database.repository")
public class ApplicationConfig {
}

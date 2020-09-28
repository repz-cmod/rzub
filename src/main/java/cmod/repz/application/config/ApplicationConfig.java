package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

@Configuration
@EnableScheduling
@EnableAsync
@EnableJpaRepositories(basePackages = "cmod.repz.application.database.repository")
public class ApplicationConfig {

    @Bean
    public ConfigModel configModel() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File("./config.js"), ConfigModel.class);
    }

    @Bean
    @Primary
    @DependsOn("configModel")
    public DataSource dataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getDatabase().getUsername())
                .password(configModel.getDatabase().getPassword())
                .url(configModel.getDatabase().getUrl())
                .build();
    }

}

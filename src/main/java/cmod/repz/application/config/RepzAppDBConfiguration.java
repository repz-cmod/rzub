package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "cmod.repz.application.database.repository.repz",
        entityManagerFactoryRef = "repzEntityManager",
        transactionManagerRef = "repzTransactionManager")
public class RepzAppDBConfiguration {
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

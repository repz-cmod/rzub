package com.github.rzub.config;

import com.github.rzub.model.SettingsModel;
import com.mongodb.ConnectionString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoRepositories(
        basePackages = "com.github.rzub.database.repository"
)
public class RZUBDBConfiguration {
    @Bean
    public MongoClientFactoryBean mongoClientFactoryBean(SettingsModel settingsModel) {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        factoryBean.setConnectionString(new ConnectionString(settingsModel.getDatabase()));
        return factoryBean;
    }
}

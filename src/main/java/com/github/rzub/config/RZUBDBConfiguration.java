package com.github.rzub.config;

import com.github.rzub.model.SettingsModel;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;


@Configuration
@EnableMongoRepositories(
        basePackages = "com.github.rzub.database.repository"
)
public class RZUBDBConfiguration extends AbstractMongoClientConfiguration {
    private final SettingsModel settingsModel;

    public RZUBDBConfiguration(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
    }

    @Bean
    public MongoClientFactoryBean mongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        factoryBean.setConnectionString(new ConnectionString(settingsModel.getDatabase()));
        return factoryBean;
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString(settingsModel.getDatabase());
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.github.rzub.database.repository");
    }

    @Override
    protected String getDatabaseName() {
        if (settingsModel.getDatabase() == null) {
            return null;
        }

        String[] split = settingsModel.getDatabase().split("/");
        return split[split.length - 1];
    }
}

package com.github.rzub.config;

import com.github.rzub.model.ConfigModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.github.rzub.database.repository",
        entityManagerFactoryRef = "rzubEntityManager",
        transactionManagerRef = "rzubTransactionManager")
public class RZUBDBConfiguration {
    @Bean("rzubDataSource")
    @Primary
    @DependsOn("configModel")
    public DataSource rzubDataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getDatabase().getUsername())
                .password(configModel.getDatabase().getPassword())
                .url(configModel.getDatabase().getUrl())
                .driverClassName(configModel.getDatabase().getDriver())
                .build();
    }

    @Bean("rzubEntityManager")
    @DependsOn("rzubDataSource")
    @Primary
    public LocalContainerEntityManagerFactoryBean rzubEntityManager(@Qualifier("rzubDataSource") DataSource rzubDataSource, ConfigModel configModel) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(rzubDataSource);
        em.setPackagesToScan("com.github.rzub.database.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", configModel.getDatabase().getHbm2ddl());
        properties.put("hibernate.dialect", configModel.getDatabase().getDialect());
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("rzubTransactionManager")
    @DependsOn("rzubEntityManager")
    @Primary
    public PlatformTransactionManager rzubTransactionManager(@Qualifier("rzubEntityManager") LocalContainerEntityManagerFactoryBean rzubEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(rzubEntityManager.getObject());
        return transactionManager;
    }
}

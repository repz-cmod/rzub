package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
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
        basePackages = "cmod.repz.application.database.repository.repz",
        entityManagerFactoryRef = "repzEntityManager",
        transactionManagerRef = "repzTransactionManager")
public class RepzAppDBConfiguration {
    @Bean("repzDataSource")
    @Primary
    @DependsOn("configModel")
    public DataSource repzDataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getDatabase().getUsername())
                .password(configModel.getDatabase().getPassword())
                .url(configModel.getDatabase().getUrl())
                .driverClassName(configModel.getDatabase().getDialect())
                .build();
    }

    @Bean("repzEntityManager")
    @DependsOn("repzDataSource")
    @Primary
    public LocalContainerEntityManagerFactoryBean repzEntityManager(@Qualifier("repzDataSource") DataSource repzDataSource, ConfigModel configModel) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(repzDataSource);
        em.setPackagesToScan("cmod.repz.application.database.entity.repz");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", configModel.getDatabase().getHbm2ddl());
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("repzTransactionManager")
    @DependsOn("repzEntityManager")
    @Primary
    public PlatformTransactionManager repzTransactionManager(@Qualifier("repzEntityManager") LocalContainerEntityManagerFactoryBean repzEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(repzEntityManager.getObject());
        return transactionManager;
    }
}

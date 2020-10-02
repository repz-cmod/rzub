package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
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
    @Bean
    @Primary
    @DependsOn("configModel")
    public DataSource repzDataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getDatabase().getUsername())
                .password(configModel.getDatabase().getPassword())
                .url(configModel.getDatabase().getUrl())
                .build();
    }

    @Bean
    @DependsOn("repzDataSource")
    public LocalContainerEntityManagerFactoryBean repzEntityManager(DataSource repzDataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(repzDataSource);
        em.setPackagesToScan("cmod.repz.application.database.entity.repz");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @DependsOn("repzEntityManager")
    public PlatformTransactionManager repzTransactionManager(LocalContainerEntityManagerFactoryBean repzEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(repzEntityManager.getObject());
        return transactionManager;
    }
}

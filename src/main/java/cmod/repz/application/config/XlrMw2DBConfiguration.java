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
        basePackages = "cmod.repz.application.database.repository.xlr.mw2",
        entityManagerFactoryRef = "xlrMw2EntityManager",
        transactionManagerRef = "xlrMw2TransactionManager")
public class XlrMw2DBConfiguration {
    @Bean
    @Primary
    @DependsOn("configModel")
    public DataSource xlrMw2DataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getDatabase().getUsername())
                .password(configModel.getDatabase().getPassword())
                .url(configModel.getDatabase().getUrl())
                .build();
    }

    @Bean
    @DependsOn("xlrMw2DataSource")
    public LocalContainerEntityManagerFactoryBean xlrMw2EntityManager(DataSource repzDataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(repzDataSource);
        em.setPackagesToScan("cmod.repz.application.database.entity.xlr");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @DependsOn("xlrMw2EntityManager")
    public PlatformTransactionManager xlrMw2TransactionManager(LocalContainerEntityManagerFactoryBean repzEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(repzEntityManager.getObject());
        return transactionManager;
    }
}

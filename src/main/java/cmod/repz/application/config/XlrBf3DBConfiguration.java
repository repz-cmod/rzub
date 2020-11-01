package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "cmod.repz.application.database.repository.xlr.bf3",
        entityManagerFactoryRef = "xlrBf3EntityManager",
        transactionManagerRef = "xlrBf3TransactionManager")
public class XlrBf3DBConfiguration {
    private final String DB_FIELD = "bf3";

    @Bean("xlrBf3DataSource")
    @DependsOn("configModel")
    public DataSource xlrBf3DataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getXlrDatabase().get(DB_FIELD).getUsername())
                .password(configModel.getXlrDatabase().get(DB_FIELD).getPassword())
                .url(configModel.getXlrDatabase().get(DB_FIELD).getUrl())
                .driverClassName("com.mysql.jdbc.Driver")
                .build();
    }

    @Bean("xlrBf3EntityManager")
    @DependsOn("xlrBf3DataSource")
    public LocalContainerEntityManagerFactoryBean xlrBf3EntityManager(@Qualifier("xlrBf3DataSource") DataSource xlrBf3DataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(xlrBf3DataSource);
        em.setPackagesToScan("cmod.repz.application.database.entity.xlr");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("xlrBf3TransactionManager")
    @DependsOn("xlrBf3EntityManager")
    public PlatformTransactionManager xlrBf3TransactionManager(@Qualifier("xlrBf3EntityManager") LocalContainerEntityManagerFactoryBean xlrBf3EntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(xlrBf3EntityManager.getObject());
        return transactionManager;
    }
}

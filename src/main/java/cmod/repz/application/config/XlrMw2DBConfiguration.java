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
        basePackages = "cmod.repz.application.database.repository.xlr.mw2",
        entityManagerFactoryRef = "xlrMw2EntityManager",
        transactionManagerRef = "xlrMw2TransactionManager")
public class XlrMw2DBConfiguration {
    private final String DB_FIELD = "mw2";

    @Bean("xlrMw2DataSource")
    @DependsOn("configModel")
    public DataSource xlrMw2DataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getXlrDatabase().get(DB_FIELD).getUsername())
                .password(configModel.getXlrDatabase().get(DB_FIELD).getPassword())
                .url(configModel.getXlrDatabase().get(DB_FIELD).getUrl())
                .driverClassName("com.mysql.jdbc.Driver")
                .build();
    }

    @Bean("xlrMw2EntityManager")
    @DependsOn("xlrMw2DataSource")
    public LocalContainerEntityManagerFactoryBean xlrMw2EntityManager(@Qualifier("xlrMw2DataSource") DataSource xlrMw2DataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(xlrMw2DataSource);
        em.setPackagesToScan("cmod.repz.application.database.entity.xlr");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("xlrMw2TransactionManager")
    @DependsOn("xlrMw2EntityManager")
    public PlatformTransactionManager xlrMw2TransactionManager(@Qualifier("xlrMw2EntityManager") LocalContainerEntityManagerFactoryBean xlrMw2EntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(xlrMw2EntityManager.getObject());
        return transactionManager;
    }
}

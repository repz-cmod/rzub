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
        basePackages = "cmod.repz.application.database.repository.xlr.bo2",
        entityManagerFactoryRef = "xlrBo2EntityManager",
        transactionManagerRef = "xlrBo2TransactionManager")
public class XlrBo2DBConfiguration {
    private final String DB_FIELD = "bo2";

    @Bean("xlrBo2DataSource")
    @DependsOn("configModel")
    public DataSource xlrBo2DataSource(ConfigModel configModel) {
        return DataSourceBuilder
                .create()
                .username(configModel.getXlrDatabase().get(DB_FIELD).getUsername())
                .password(configModel.getXlrDatabase().get(DB_FIELD).getPassword())
                .url(configModel.getXlrDatabase().get(DB_FIELD).getUrl())
                .driverClassName("com.mysql.jdbc.Driver")
                .build();
    }

    @Bean("xlrBo2EntityManager")
    @DependsOn("xlrBo2DataSource")
    public LocalContainerEntityManagerFactoryBean xlrBo2EntityManager(@Qualifier("xlrBo2DataSource") DataSource xlrBo2DataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(xlrBo2DataSource);
        em.setPackagesToScan("cmod.repz.application.database.entity.xlr");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("xlrBo2TransactionManager")
    @DependsOn("xlrBo2EntityManager")
    public PlatformTransactionManager xlrBo2TransactionManager(@Qualifier("xlrBo2EntityManager") LocalContainerEntityManagerFactoryBean xlrBo2EntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(xlrBo2EntityManager.getObject());
        return transactionManager;
    }
}

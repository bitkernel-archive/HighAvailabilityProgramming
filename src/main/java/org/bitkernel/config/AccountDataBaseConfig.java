package org.bitkernel.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Primary
@Configuration
@EnableTransactionManagement
public class AccountDataBaseConfig extends AbstractDBConfig {

    @Bean(value = "accountEntityManager")
    public EntityManager entityManager(@Qualifier(value = "accountSessionFactory") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }
    @Bean(name = "accountDataSource")
    public DataSource accountDataSource() {
        return getDataSource();
    }

    @Bean(name = "accountSessionFactory")
    public FactoryBean<SessionFactory> accountFactory() {
        return getSessionFactory(accountDataSource());
    }

    @Bean(name = "accountTransactionManager")
    public PlatformTransactionManager accountTransactionManager() throws Exception{
        return getTransactionManager(accountFactory().getObject());
    }
}

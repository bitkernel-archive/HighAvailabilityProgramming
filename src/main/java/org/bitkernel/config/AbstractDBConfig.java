package org.bitkernel.config;

import com.sun.istack.NotNull;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

public class AbstractDBConfig {
    /** DB config field */
    protected String cfgNamePrefix = "";
    protected String driverClassName = "com.mysql.cj.jdbc.Driver";
    protected String validationQuery = "SELECT 1";
    protected int maxActive = 500;
    protected int minIdle = 10;
    protected long idleTimeout = 60000;
    protected int txTimeout = 1800;

    /** hibernate config field */
    protected String dialect = "org.hibernate.dialect.MySQL55Dialect";
    protected String hbm2ddlAuto = "update";
    protected boolean showSql = false;
    protected boolean generateStatistics = false;

    @NotNull
    public DataSource getDataSource() {
        String host = "127.0.0.1";
        String dbInst = "mysql";
        String username = "root";
        String password = "1234";

        String jdbcTpl = "jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false&serverTimezone=Asia/Shanghai";
        String dbUrl = String.format(jdbcTpl, host, dbInst);

        HikariDataSource datasource = new HikariDataSource();
        datasource.setPoolName(String.format("hikari-pool-%s", Strings.isBlank(cfgNamePrefix)));
        datasource.setJdbcUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setIdleTimeout(idleTimeout);
        datasource.setDriverClassName(driverClassName);
        datasource.setConnectionInitSql(validationQuery);
        datasource.setMinimumIdle(minIdle);
        datasource.setMaximumPoolSize(maxActive);
        return datasource;
    }

    @NotNull
    public Properties getHibernateProperties() {
        Properties props = new Properties();

        props.put("hibernate.dialect", dialect);
        props.put("hibernate.show_sql", showSql);
        props.put("hibernate.format_sql", false);
        props.put("hibernate.generate_statistics", generateStatistics);
        props.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        props.put("hibernate.connection.isolation", java.sql.Connection.TRANSACTION_READ_COMMITTED);
        props.put("hibernate.use_sql_comments", true);

        props.put("hibernate.cache.use_second_level_cache", false);
        props.put("hibernate.cache.use_query_cache", false);
        props.put("hibernate.connection.CharSet", "utf8");
        props.put("hibernate.connection.characterEncoding", "utf8");
        props.put("hibernate.connection.useUnicode", true);
        props.put("hibernate.autoReconnect", true);
        return props;
    }

    @NotNull
    public FactoryBean<SessionFactory> getSessionFactory(@NotNull DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();

        String scanPackages = "org.bitkernel";
        sessionFactoryBean.setPackagesToScan(scanPackages.split(","));

        sessionFactoryBean.setHibernateProperties(getHibernateProperties());
        sessionFactoryBean.setDataSource(dataSource);
        return sessionFactoryBean;
    }

    @NotNull
    public PlatformTransactionManager getTransactionManager(@NotNull SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
        transactionManager.setDefaultTimeout(txTimeout);
        return transactionManager;
    }
}

package com.zuihuibao.web.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by millions on 2016/8/23.
 * 配置数据源
 */

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    private
    @Value("${jdbc.driverClassName}")
    String driverClassName;
    private
    @Value("${web.db.url}")
    String url;

    private
    @Value("${web.db.username}")
    String username;

    private
    @Value("${web.db.password}")
    String password;

    private
    @Value("${druid.pool.size.init}")
    int druidPoolSizeInit;

    private
    @Value("${druid.pool.size.min}")
    int druidPoolSizeMin;
    private
    @Value("${druid.pool.size.max}")
    int druidPoolSizeMax;
    private
    @Value("${druid.filters}")
    String druidFilters;


    @Bean
    DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setInitialSize(druidPoolSizeInit);
        druidDataSource.setMinIdle(druidPoolSizeMin);
        druidDataSource.setMaxActive(druidPoolSizeMax);
        druidDataSource.setFilters(druidFilters);
        return druidDataSource;
    }

    @Bean(name = "sqlSessionFactoryBean")
    SqlSessionFactoryBean sqlSessionFactoryBean() throws SQLException {
        SqlSessionFactoryBean sqlSessionFactoryBean
                = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        //todo: typeAliasesPackage
        sqlSessionFactoryBean.setTypeAliasesPackage("");
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{new ClassPathResource("mapper/*.xml")});
        return sqlSessionFactoryBean;
    }

    @Bean
    MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer
                = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage("com.zuihuibao.web.dao");
        return mapperScannerConfigurer;
    }

    @Bean
    DataSourceTransactionManager transactionManager() throws SQLException {
        DataSourceTransactionManager transactionManager
                = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}

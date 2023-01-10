package com.adennet.config.postgres;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PostgresDbClientConfig {

    @Bean("usageMgmtDatasource")
    public DataSource dataSource(@Qualifier("usageMgmtDbProperties") DataSourceProperties usageMgmtProdDbProperties) {
        return usageMgmtProdDbProperties.initializeDataSourceBuilder().build();
    }

    @Bean("usageMgmtDbClient")
    public JdbcTemplate usageMgmtDbClient(@Qualifier("usageMgmtDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}

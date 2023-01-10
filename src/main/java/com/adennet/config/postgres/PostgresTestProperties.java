package com.adennet.config.postgres;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class PostgresTestProperties {

    @Bean("usageMgmtDbProperties")
    @ConfigurationProperties(prefix = "test.datasource")
    public DataSourceProperties usageMgmtTestDbProperties() {
        return new DataSourceProperties();
    }
}

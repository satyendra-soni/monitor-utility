package com.adennet.config.postgres;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class PostgresProdProperties {
    @Bean("usageMgmtDbProperties")
    @ConfigurationProperties(prefix = "prod.datasource")
    public DataSourceProperties usageMgmtProdDbProperties() {
        return new DataSourceProperties();
    }
}

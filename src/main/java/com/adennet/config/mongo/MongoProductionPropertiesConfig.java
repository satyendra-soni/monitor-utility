package com.adennet.config.mongo;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class MongoProductionPropertiesConfig {

    @Bean("radiusDbProperties")
    @Lazy
    @ConfigurationProperties(prefix = "prod.mongodb.radius")
    public MongoProperties radiusDbProperties() {
        return new MongoProperties();
    }


    @Bean("mediationProperties")
    @Lazy
    @ConfigurationProperties(prefix = "prod.mongodb.mediation")
    public MongoProperties mediationProperties() {
        return new MongoProperties();
    }
}

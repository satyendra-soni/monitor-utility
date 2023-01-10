package com.adennet.config.mongo;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

@Profile("test")
@Configuration
public class MongoTestPropertiesConfig {

    @Bean("radiusDbProperties")
    @ConfigurationProperties(prefix = "test.mongodb.radius")
    @Lazy
    public MongoProperties radiusDbProperties() {
        return new MongoProperties();
    }


    @Bean("mediationProperties")
    @ConfigurationProperties(prefix = "test.mongodb.mediation")
    @Lazy
    public MongoProperties mediationProperties() {
        return new MongoProperties();
    }

}

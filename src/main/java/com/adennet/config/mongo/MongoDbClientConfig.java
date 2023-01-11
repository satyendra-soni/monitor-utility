package com.adennet.config.mongo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.Base64Utils;

@Configuration
public class MongoDbClientConfig {
    @Bean("radiusDatabaseFactory")
    @Lazy
    public MongoDatabaseFactory radiusDatabaseFactory(@Qualifier("radiusDbProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(
                new String(Base64Utils.decode(mongoProperties.getUri().getBytes()))
        );
    }

    @Bean("mediationDatabaseFactory")
    @Lazy
    public MongoDatabaseFactory mediationDatabaseFactory(@Qualifier("mediationProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(
                new String(Base64Utils.decode(mongoProperties.getUri().getBytes()))
        );
    }

    @Bean("radiusDbMongoClient")
    @Lazy
    public MongoTemplate radiusDbMongoClient(@Qualifier("radiusDatabaseFactory") MongoDatabaseFactory grafanaMongoDatabaseFactory) {
        return new MongoTemplate(grafanaMongoDatabaseFactory);
    }

    @Bean("mediationDbMongoClient")
    @Lazy
    public MongoTemplate mediationDbMongoClient(@Qualifier("mediationDatabaseFactory") MongoDatabaseFactory grafanaMongoDatabaseFactory) {
        return new MongoTemplate(grafanaMongoDatabaseFactory);
    }

}

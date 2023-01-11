package com.adennet.config.postgres;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Profile("prod")
@Configuration
public class PostgresProdProperties {
    @Component("usageMgmtDbProperties")
    @ConfigurationProperties(prefix = "prod.datasource")
    class TestDataSourceProperties extends DataSourceProperties {

        @Override
        public void setUsername(String username) {
            String decodedUsername = new String(Base64Utils.decode(username.getBytes()));
            super.setUsername(decodedUsername);
        }

        @Override
        public void setPassword(String password) {
            String decodedPassword = new String(Base64Utils.decode(password.getBytes()));
            super.setPassword(decodedPassword);
        }

        @Override
        public void setUrl(String url) {
            String decodedUrl = new String(Base64Utils.decode(url.getBytes()));
            super.setUrl(decodedUrl);
        }
    }
}

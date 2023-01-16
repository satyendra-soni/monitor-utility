package com.adennet.config;

import com.adennet.dto.ServerDetail;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "billinghub.servers")
public class ServerRegistry {

    private Map<String, ServerDetail> serverinfo;
}

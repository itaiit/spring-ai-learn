package io.itaiit.config;

import io.itaiit.service.PingService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(PingService pingService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(pingService)
                .build();
    }

}

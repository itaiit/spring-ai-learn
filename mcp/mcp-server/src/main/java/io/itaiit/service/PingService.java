package io.itaiit.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PingService {

    @Tool(description = "用于测试服务是否正常的端点")
    public String ping() {
        return "pong";
    }

}

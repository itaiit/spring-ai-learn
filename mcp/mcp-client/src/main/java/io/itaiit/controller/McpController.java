package io.itaiit.controller;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final List<McpSyncClient> mcpSyncClientList;
    private final ToolCallbackProvider toolCallbackProvider;
    private final ChatClient chatClient;

    public McpController(List<McpSyncClient> mcpSyncClientList, ToolCallbackProvider toolCallbackProvider, ChatClient.Builder chatClientBuilder) {
        this.mcpSyncClientList = mcpSyncClientList;
        this.toolCallbackProvider = toolCallbackProvider;
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();

    }

    @GetMapping("/getAllClients")
    public List<McpSyncClient> getMcpSyncClientList() {
        return mcpSyncClientList;
    }

    @GetMapping("/getAllTools")
    public ToolCallback[] getAllTools() {
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        return toolCallbacks;
    }

    @GetMapping("/getChat")
    public String getChatClient(@RequestParam("text") String text) {
        return chatClient.prompt(text)
//                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();
    }

}

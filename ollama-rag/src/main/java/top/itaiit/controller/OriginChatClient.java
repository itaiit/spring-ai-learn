package top.itaiit.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import top.itaiit.service.VectorStoreService;
import top.itaiit.tool.DateTimeTools;

import java.util.List;

/**
 * 使用ChatClient来实现
 */
@RestController()
@RequestMapping("/cc")
public class OriginChatClient {

    private final ChatClient chatClient;
    private final VectorStoreService vectorStoreService;

    public OriginChatClient(ChatClient.Builder chatClientBuilder, VectorStoreService vectorStoreService) {
        this.chatClient = chatClientBuilder
                .defaultTools(new DateTimeTools())
                .build();
        this.vectorStoreService = vectorStoreService;
    }

    /**
     * 直接返回响应信息
     *
     * @param message
     * @return
     */
    @GetMapping("/origin/ai/generate")
    public String generate(@RequestParam(name = "message", defaultValue = "生成一个可以用于每日一言的句子") String message) {
        Message tip = UserMessage.builder()
                .text(message)
                .build();
        Prompt prompt = Prompt.builder()
                .messages(tip)
                .chatOptions(OllamaOptions.builder() // 这里需要使用ollama的options，才会有ollama的一些配置信息
//                        .model("gemma3:4b") // 在运行的时候指定使用的模型
                        .topK(8)
                        .build()
                )
                .build();
        String answer = chatClient.prompt(prompt)
                .call()
                .content();

        return answer;
    }

    /**
     * 流式的返回响应
     *
     * @param message
     * @param httpServletResponse
     * @return
     */
    @GetMapping("/origin/ai/genFlux")
    public Flux<String> generateFlux(@RequestParam(name = "message", defaultValue = "生成一个可以用于每日一言的句子") String message
            , HttpServletResponse httpServletResponse) {
        // 设置响应头，如果不设置的话中文打印乱码
        httpServletResponse.setCharacterEncoding("UTF-8");
        Message tip = UserMessage.builder()
                .text(message)
                .build();
        Prompt prompt = Prompt.builder()
                .messages(tip)
                .chatOptions(ChatOptions.builder()
                        .topK(8)
                        .build()
                )
                .build();
        Flux<String> content = chatClient.prompt(prompt)
                .stream()
                .content();

        return content;
    }

    @PostMapping("/origin/addVector")
    public List<Document> addVector(@RequestBody List<String> documents) {
        return vectorStoreService.saveToVectorStore(documents);
    }

}

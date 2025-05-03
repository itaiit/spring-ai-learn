package top.itaiit.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 使用ChatClient来实现
 */
@RestController()
@RequestMapping("/cc")
public class OriginChatClient {

    private final ChatClient chatClient;

    public OriginChatClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 直接返回响应信息
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
                .chatOptions(ChatOptions.builder()
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

}

package top.itaiit.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/cm")
public class OriginChatClient {

    private final ChatClient chatClient;

    public OriginChatClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @GetMapping("/origin/ai/generate")
    public String generate(@RequestParam(name = "message", defaultValue = "你是谁") String message) {
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

}

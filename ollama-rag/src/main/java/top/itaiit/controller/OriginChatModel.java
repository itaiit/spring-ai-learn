package top.itaiit.controller;

import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 使用ChatModel来实现
 */
@RestController("/cm")
public class OriginChatModel {

    private final ChatModel chatModel;

    public OriginChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
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
        return getContentFromChatResponse(chatModel.call(prompt));
    }

    private String getContentFromChatResponse(@Nullable ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AbstractMessage::getText)
                .orElse(null);
    }

}

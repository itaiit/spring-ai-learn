package top.itaiit.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import top.itaiit.service.VectorStoreService;
import top.itaiit.tool.DateTimeTools;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final ChatModel chatModel;
    private final VectorStoreService vectorStoreService;
    private final ChatClient chatClient;

    @Autowired
    public ChatController(ChatModel chatModel, VectorStoreService vectorStoreService) {
        this.chatModel = chatModel;
        this.vectorStoreService = vectorStoreService;
        chatClient = ChatClient.builder(chatModel)
//                .defaultTools() // 可以通过此方法设置默认的工具
                .build();
    }

    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        ChatClient.CallResponseSpec called = chatClient.prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStoreService.getVectorStore(), SearchRequest.builder().similarityThreshold(0.8d).topK(6).build()))
                .user(message)
                .call();
        return Map.of("generation", called.content());
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }

    @PostMapping("/ai/saveToVectorStore")
    public List<Document> getVectorStore(@RequestBody List<String> documents) {
        return vectorStoreService.saveToVectorStore(documents);
    }

    @GetMapping("/ai/dateTool")
    public String getCurrentDateTime() {
        return chatClient
                .prompt("current time")
                .tools(new DateTimeTools())
                .call()
                .content();
    }

}
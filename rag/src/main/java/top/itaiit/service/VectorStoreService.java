package top.itaiit.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {
    @Autowired
    private VectorStore vectorStore;

    public VectorStore getVectorStore() {
        return vectorStore;
    }

    public List<Document> saveToVectorStore(List<String> documents) {
        List<Document> documentsToAdd = documents.stream().map(word -> new Document(word, Map.of("meta1", "meta1"))).toList();
//        List<Document> documentsToAdd = List.of(
//                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
//                new Document("The World is Big and Salvation Lurks Around the Corner"),
//                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
        // Add the documents to Redis
        vectorStore.add(documentsToAdd);

        // Retrieve documents similar to a query
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());

        return results;
    }

}

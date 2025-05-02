package top.itaiit.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class VectorStoreService {
    @Autowired
    private VectorStore vectorStore;

    public VectorStore getVectorStore() {
        return vectorStore;
    }

    public List<Document> saveToVectorStore(List<String> documents) {
        List<Document> documentsToAdd = documents.stream().map(word -> new Document(word, Collections.emptyMap())).toList();
        // Add the documents to Redis
        vectorStore.add(documentsToAdd);
        // Retrieve documents similar to a query
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        return results;
    }

}

package com.example.mydhissia.rag;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagIntegrationTest {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private RagQueryService ragQueryService;

    @Test
    void testIngestionAndQuery() {
        // 1. Manually add a doc
        Document doc = new Document("MyDHISSIA is a Spring Boot project.");
        vectorStore.add(List.of(doc));

        // 2. Query via Service
        String answer = ragQueryService.ask("What is MyDHISSIA?");
        
        // 3. Simple assertion (assuming LLM works or mock/stub)
        // Since we are using real LLM, we can just check if it returns non-empty string.
        assertThat(answer).isNotBlank();
    }
}

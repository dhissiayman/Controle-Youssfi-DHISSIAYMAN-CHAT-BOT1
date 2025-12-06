package com.example.mydhissia.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagQueryService {

    private static final Logger log = LoggerFactory.getLogger(RagQueryService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagQueryService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String ask(String query) {
        log.info("RAG Query: {}", query);

        // 1. Retrieve similar documents
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(query).withTopK(3));
        
        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        log.info("Found {} relevant documents.", similarDocuments.size());
        log.debug("Context: {}", context);

        if (similarDocuments.isEmpty()) {
            return "I couldn't find any information in my knowledge base regarding your query.";
        }

        // 2. Construct Prompt
        String systemPromptText = """
                You are a helpful assistant. Use the following information to answer the user's question.
                If the information is not sufficient to answer, say so.
                
                Context:
                {context}
                """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPromptText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("context", context));
        
        // 3. Call LLM
        String answer = chatClient.call(new Prompt(List.of(systemMessage, new UserMessage(query)))).getResult().getOutput().getContent();
        return "[Source: RAG] " + answer;
    }
}

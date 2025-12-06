package com.example.mydhissia.ai;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ConversationMemoryService {

    private final EmbeddingService embeddingService;
    
    // In-memory store: ChatId -> List of MemoryEntries
    private final Map<Long, List<MemoryEntry>> memoryStore = new ConcurrentHashMap<>();

    public ConversationMemoryService(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public void saveMessage(Long chatId, String role, String content) {
        List<Double> vector = embeddingService.embed(content);
        MemoryEntry entry = new MemoryEntry(role, content, vector, System.currentTimeMillis());
        memoryStore.computeIfAbsent(chatId, k -> new ArrayList<>()).add(entry);
    }

    public List<String> retrieveRelevantMessages(Long chatId, String queryText, int topK) {
        if (!memoryStore.containsKey(chatId)) {
            return Collections.emptyList();
        }

        List<Double> queryVector = embeddingService.embed(queryText);
        List<MemoryEntry> history = memoryStore.get(chatId);

        // Simple cosine similarity sort
        return history.stream()
                .sorted((a, b) -> Double.compare(cosineSimilarity(b.vector, queryVector), cosineSimilarity(a.vector, queryVector)))
                .limit(topK)
                .map(entry -> entry.role + ": " + entry.content)
                .collect(Collectors.toList());
    }
    
    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1 == null || v2 == null || v1.size() != v2.size()) return 0.0;
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static class MemoryEntry {
        String role;
        String content;
        List<Double> vector;
        long timestamp;

        public MemoryEntry(String role, String content, List<Double> vector, long timestamp) {
            this.role = role;
            this.content = content;
            this.vector = vector;
            this.timestamp = timestamp;
        }
    }
}

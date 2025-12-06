package com.example.mydhissia.ai;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingClient embeddingClient;

    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }

    public List<Double> embed(String text) {
        return embeddingClient.embed(text);
    }
}

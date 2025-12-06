package com.example.mydhissia.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class RagIngestionService {

    private static final Logger log = LoggerFactory.getLogger(RagIngestionService.class);

    private final VectorStore vectorStore;

    @Value("classpath*:rag-data/*.txt")
    private Resource[] resources;

    @Value("${spring.ai.vectorstore.path:vectorstore.json}")
    private String vectorStorePath;

    public RagIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {
        File storeFile = new File(vectorStorePath);
        if (storeFile.exists()) {
            log.info("Loading existing vector store from {}", vectorStorePath);
            if (vectorStore instanceof SimpleVectorStore) {
                ((SimpleVectorStore) vectorStore).load(storeFile);
            }
        } else {
            log.info("No existing vector store found at {}. Performing initial ingestion...", vectorStorePath);
            ingest();
        }
    }

    public void ingest() {
        log.info("Starting RAG ingestion...");
        List<Document> documents = new ArrayList<>();

        if (resources == null || resources.length == 0) {
            log.warn("No resources found in rag-data/");
            return;
        }

        for (Resource resource : resources) {
            try {
                log.info("Processing file: {}", resource.getFilename());
                TextReader textReader = new TextReader(resource);
                documents.addAll(textReader.get());
            } catch (Exception e) {
                log.error("Failed to read resource: {}", resource.getFilename(), e);
            }
        }

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);

        log.info("Ingesting {} chunks into VectorStore", splitDocuments.size());
        vectorStore.add(splitDocuments);

        if (vectorStore instanceof SimpleVectorStore) {
            log.info("Saving vector store to {}", vectorStorePath);
            ((SimpleVectorStore) vectorStore).save(new File(vectorStorePath));
        }

        log.info("RAG ingestion complete.");
    }
}

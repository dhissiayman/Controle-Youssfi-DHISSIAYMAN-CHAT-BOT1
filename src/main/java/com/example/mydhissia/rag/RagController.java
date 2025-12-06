package com.example.mydhissia.rag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/rag")
public class RagController {

    private final RagIngestionService ragIngestionService;

    public RagController(RagIngestionService ragIngestionService) {
        this.ragIngestionService = ragIngestionService;
    }

    @PostMapping("/reindex")
    public String reindex() {
        ragIngestionService.ingest();
        return "RAG ingestion started. Check logs for details.";
    }
}

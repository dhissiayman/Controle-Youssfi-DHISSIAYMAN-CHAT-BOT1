package com.example.mydhissia.mcp.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TavilyToolClient {

    private static final String TAVILY_SEARCH_URL = "https://api.tavily.com/search";

    private final RestTemplate restTemplate;
    private final String apiKey;

    public TavilyToolClient(RestTemplateBuilder restTemplateBuilder,
                            @Value("${TAVILY_API_KEY}") String apiKey) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiKey = apiKey;
    }

    /**
     * Simple wrapper over Tavily Search API.
     * @param query the search query
     * @return raw JSON string from Tavily
     */
    public String search(String query) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Tavily API key is missing. Please set TAVILY_API_KEY env variable.";
        }

        // Request body – you can add more params later (time_range, topic, etc.)
        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        body.put("include_answer", true);  // we want Tavily's summarized answer
        body.put("max_results", 3);        // keep response small

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // "Authorization: Bearer <key>"

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                TAVILY_SEARCH_URL,
                entity,
                String.class
        );

        return response.getBody();
    }
}

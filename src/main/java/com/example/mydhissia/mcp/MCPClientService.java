package com.example.mydhissia.mcp;

import com.example.mydhissia.config.MCPConfig;
import com.example.mydhissia.mcp.tools.TimeToolClient;
import com.example.mydhissia.mcp.tools.TavilyToolClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MCPClientService {

    private static final Logger log = LoggerFactory.getLogger(MCPClientService.class);

    private final MCPConfig mcpConfig;
    private final TimeToolClient timeToolClient;
    private final TavilyToolClient tavilyToolClient;
    private final com.example.mydhissia.rag.RagQueryService ragQueryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MCPClientService(
            MCPConfig mcpConfig,
            TimeToolClient timeToolClient,
            TavilyToolClient tavilyToolClient,
            com.example.mydhissia.rag.RagQueryService ragQueryService
    ) {
        this.mcpConfig = mcpConfig;
        this.timeToolClient = timeToolClient;
        this.tavilyToolClient = tavilyToolClient;
        this.ragQueryService = ragQueryService;
    }

    public String callTool(String toolName, String jsonArgs) {
        log.info("MCP tool requested: {} with args: {}", toolName, jsonArgs);

        if ("getTime".equalsIgnoreCase(toolName)) {
            log.info("Using TimeToolClient.getCurrentTime()");
            return timeToolClient.getCurrentTime();
        }

        if ("tavilySearch".equalsIgnoreCase(toolName)) {
            String query = extractQueryFromArgs(jsonArgs);
            log.info("Using TavilyToolClient.search() with query: {}", query);
            if (query == null || query.isBlank()) {
                return "Missing 'query' argument for tavilySearch tool.";
            }
            return tavilyToolClient.search(query);
        }

        if ("ragSearch".equalsIgnoreCase(toolName)) {
            String query = extractQueryFromArgs(jsonArgs);
            log.info("Using RagQueryService.ask() with query: {}", query);
            if (query == null || query.isBlank()) {
                return "Missing 'query' argument for ragSearch tool.";
            }
            return ragQueryService.ask(query);
        }

        log.warn("MCP tool not found: {}", toolName);
        return "Tool not found on MCP server.";
    }

    private String extractQueryFromArgs(String jsonArgs) {
        if (jsonArgs == null || jsonArgs.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(jsonArgs);
            JsonNode queryNode = node.get("query");
            return queryNode != null ? queryNode.asText() : null;
        } catch (Exception e) {
            log.error("Failed to parse jsonArgs for MCP tool: {}", jsonArgs, e);
            return null;
        }
    }
}

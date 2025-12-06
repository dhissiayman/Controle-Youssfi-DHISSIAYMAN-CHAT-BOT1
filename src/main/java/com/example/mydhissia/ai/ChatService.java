package com.example.mydhissia.ai;

import com.example.mydhissia.mcp.MCPClientService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ConversationMemoryService conversationMemoryService;
    private final MCPClientService mcpClientService;
    private final com.example.mydhissia.rag.RagQueryService ragQueryService;

    public ChatService(ChatClient chatClient,
                       ConversationMemoryService conversationMemoryService,
                       MCPClientService mcpClientService,
                       com.example.mydhissia.rag.RagQueryService ragQueryService) {
        this.chatClient = chatClient;
        this.conversationMemoryService = conversationMemoryService;
        this.mcpClientService = mcpClientService;
        this.ragQueryService = ragQueryService;
    }

    public String answerWithRag(Long chatId, String userMessage) {
        return ragQueryService.ask(userMessage);
    }

    public String handleUserMessage(Long chatId, String userMessage) {
        // 1. Retrieve relevant history
        List<String> history = conversationMemoryService.retrieveRelevantMessages(chatId, userMessage, 5);
        String context = String.join("\n", history);

        // 2. Check if tools are needed (Naive implementation for demo: Keyword matching or simple heuristic)
        // In a real MCP setup, the LLM would output a tool call structure, we would parse it, and then call the MCP tool.
        // For this demo, let's inject a "Time Tool" check or generic MCP call if "time" is mentioned.
        String toolOutput = "";
        if (userMessage.toLowerCase().contains("time")) {
             toolOutput += "Tool 'getTime' output: " + mcpClientService.callTool("getTime", "{}") + "\n";
        }

        if (userMessage.toLowerCase().contains("search") || userMessage.toLowerCase().contains("tavily")) {
            // Simple JSON construction with quote escaping
            String escapedQuery = userMessage.replace("\"", "\\\"");
            String jsonArgs = "{\"query\": \"" + escapedQuery + "\"}";
            toolOutput += "Tool 'tavilySearch' output: " + mcpClientService.callTool("tavilySearch", jsonArgs) + "\n";
        }

        // 3. Construct prompt
        String systemPrompt = "You are a helpful AI assistant named MyDHISSIA. " +
                "You have access to previous conversation context and some tools.\n" +
                "Context:\n" + context + "\n" +
                "Tool Outputs:\n" + toolOutput + "\n" +
                "User Message: " + userMessage + "\n" +
                "Respond naturally.";

        // 4. Call LLM
        String response = chatClient.call(systemPrompt);

        // 5. Save new turn to memory
        conversationMemoryService.saveMessage(chatId, "user", userMessage);
        conversationMemoryService.saveMessage(chatId, "assistant", response);

        return response;
    }
}

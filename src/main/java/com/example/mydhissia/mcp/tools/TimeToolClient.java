package com.example.mydhissia.mcp.tools;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeToolClient {
    public String getCurrentTime() {
        // In a real agent, this might call a remote MCP tool.
        // For the "local tool" requirement, we return local time.
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}

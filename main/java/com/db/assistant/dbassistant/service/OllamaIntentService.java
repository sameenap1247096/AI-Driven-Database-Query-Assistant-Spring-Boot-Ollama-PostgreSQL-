package com.db.assistant.dbassistant.service;


import com.db.assistant.dbassistant.dto.AIRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

@Service
public class OllamaIntentService {

    @Value("${ollama.endpoint}")
    private String ollamaEndpoint;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final ObjectMapper mapper = new ObjectMapper();

    public AIRequest getIntent(String userQuery) {
        try {
            String prompt = "You are an assistant that extracts a fixed JSON intent from user requests about transactions.\n" +
                    "Return exactly one JSON object and nothing else.\n\n" +
                    "Schema:\n" +
                    "{\n" +
                    "  \"action\": one of [\"get_status\",\"count_failed\",\"list_transactions\"],\n" +
                    "  \"transactionId\": string or null,\n" +
                    "  \"limit\": integer or null\n" +
                    "}\n\n" +
                    "Rules:\n" +
                    "- If user asks for single transaction status, action=\"get_status\" and fill transactionId.\n" +
                    "- If user asks for counts (e.g., how many failed today), action=\"count_failed\".\n" +
                    "- If user asks for a list, action=\"list_transactions\" and set limit if asked.\n" +
                    "- Fields that are not applicable must be null.\n\n" +
                    "User query: " + userQuery;

            String requestJson = mapper.writeValueAsString(Map.of(
                    "model", ollamaModel,
                    "prompt", prompt,
                    "stream", false
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaEndpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                String err = new String(response.body().readAllBytes());
                throw new RuntimeException("Ollama error: " + response.statusCode() + " - " + err);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));
            String line;
            StringBuilder jsonBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode node = mapper.readTree(line);
                    // if model streams partial text in "response", append
                    if (node.has("response")) {
                        jsonBuilder.append(node.get("response").asText());
                    } else {
                        jsonBuilder.append(line);
                    }
                } catch (Exception ignored) { }
            }
            String json = jsonBuilder.toString().trim();
            if (json.startsWith("```")) {
                // strip code fences
                json = json.replaceAll("^```+|```+$", "").trim();
            }


            // parse into AIRequest
            AIRequest aiRequest = mapper.readValue(json, AIRequest.class);
            return aiRequest;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get intent from Ollama: " + e.getMessage(), e);
        }
    }
}

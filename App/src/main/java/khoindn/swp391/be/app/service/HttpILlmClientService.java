// src/main/java/khoindn/swp391/be/app/service/HttpILlmClientService.java
package khoindn.swp391.be.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.model.Response.LlmResult;
import khoindn.swp391.be.app.pojo.Message;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HttpILlmClientService implements ILlmClientService {

    private static final Logger log = LoggerFactory.getLogger(HttpILlmClientService.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    @Value("${openai.api.key}") // lấy từ env OPENAI_API_KEY
    private String apiKey;

    @Value("${openai.api.base:https://api.openai.com}")
    private String baseUrl;

    @Value("${openai.model:gpt-3.5-turbo}")

    private String model;

    @Value("${openai.timeout-ms:60000}")
    private long timeoutMs;

    public HttpILlmClientService(ObjectMapper mapper) {
        this.mapper = mapper;
        this.client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofMillis(60000))
                .connectTimeout(Duration.ofMillis(15000))
                .readTimeout(Duration.ofMillis(60000))
                .writeTimeout(Duration.ofMillis(60000))
                .build();
    }

    @Override
    public LlmResult chat(List<Message> messages) {
        log.info("OpenAI base={} | model={} | keyLen={}",
                baseUrl, model, (apiKey == null ? 0 : apiKey.length()));

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("OPENAI_API_KEY is missing (property openai.api.key).");
        }

        // 1) Build body
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages.stream().map(Message::toMap).toList());
        body.put("temperature", 0.2);

        final String json;
        try {
            json = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialize request body failed", e);
        }

        // 2) Build request
        Request request = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        // 3) Execute + parse
        try (Response resp = client.newCall(request).execute()) {
            String respBody = resp.body() != null
                    ? new String(resp.body().bytes(), StandardCharsets.UTF_8)
                    : "";

            if (!resp.isSuccessful()) {
                // log lỗi chi tiết để debug (401/400/429…)
                log.error("OpenAI error {} {} | body={}", resp.code(), resp.message(), respBody);
                throw new RuntimeException("LLM HTTP " + resp.code() + ": " + resp.message());
            }

            // Ưu tiên: map thẳng vào LlmResult (schema bạn đã định nghĩa)
            LlmResult mapped = null;
            try {
                mapped = mapper.readValue(respBody, LlmResult.class);
            } catch (JsonProcessingException ignore) {
                // sẽ parse thủ công bên dưới
            }
            if (mapped != null && mapped.firstText() != null && !mapped.firstText().isBlank()) {
                return mapped;
            }

            // Fallback: parse thủ công tối thiểu -> không set usage (tránh phụ thuộc setter)
            JsonNode root = mapper.readTree(respBody);
            String content = root.path("choices").isArray() && root.path("choices").size() > 0
                    ? root.path("choices").get(0).path("message").path("content").asText("")
                    : "";

            LlmResult manual = new LlmResult();
            LlmResult.Msg msg = new LlmResult.Msg();
            msg.setRole("assistant");
            msg.setContent(content);
            LlmResult.Choice ch = new LlmResult.Choice();
            ch.setMessage(msg);
            manual.setChoices(List.of(ch));
            return manual;

        } catch (IOException e) {
            throw new RuntimeException("HTTP call to OpenAI failed", e);
        }
    }
}

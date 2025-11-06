package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.model.Request.ChatRequest;
import khoindn.swp391.be.app.model.Response.ChatResponse;
import khoindn.swp391.be.app.model.Response.LlmResult;
import khoindn.swp391.be.app.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatGPTService {

    private static final Logger log = LoggerFactory.getLogger(ChatGPTService.class);

    private final ILlmClientService ILlmClientService;
    private final KnowledgeSearchService knowledgeSearchService;

    public ChatGPTService(ILlmClientService ILlmClientService, KnowledgeSearchService knowledgeSearchService) {
        this.ILlmClientService = ILlmClientService;
        this.knowledgeSearchService = knowledgeSearchService;
    }

    public ChatResponse generateReply(ChatRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            return ChatResponse.builder()
                    .reply("Hi! Please provide a message.")
                    .createdAt(OffsetDateTime.now())
                    .build();
        }

        final int TOP_K = 5;
        List<String> dbContexts = safeList(knowledgeSearchService.searchRelated(request.getMessage(), TOP_K));
        List<String> clientCtx  = safeList(request.getContext());

        // Gộp & khử trùng (giữ thứ tự)
        List<String> allContexts = new ArrayList<>(dedupeKeepOrder(dbContexts));
        allContexts.addAll(dedupeKeepOrder(clientCtx));

        // ✅ Coi CẢ DB lẫn client context là "internal"
        boolean hasAnyContext = !allContexts.isEmpty();

        // Ghép block context (giới hạn theo số từ để tiết kiệm token)
        String contextBlock = buildContextBlockWordLimited(allContexts, 180); // ~1200–1400 chars
        if (!hasAnyContext) {
            contextBlock = "(no internal context found)";
        }

        // Messages
        List<Message> messages = new ArrayList<>();
        messages.add(Message.system(
                "You are an assistant for an EV Co-ownership system.\n" +
                        "- Use ONLY the provided CONTEXT. If the information is missing in CONTEXT, say you are not sure and suggest what data is needed.\n" +
                        "- Be concise and factual. If you reference a specific fact, cite the Source #.\n" +
                        "- Do NOT fabricate IDs, dates, or policies that are not explicitly in CONTEXT.\n" +
                        "- If no internal context is provided, answer generally but clearly state that internal data was not found."
        ));

        String userPayload = """
                === CONTEXT START ===
                %s
                === CONTEXT END ===

                USER QUESTION:
                %s
                """.formatted(contextBlock, request.getMessage());
        messages.add(Message.user(userPayload));

        // Gọi model an toàn + fallback thông minh
        String reply;
        Integer promptT = null, completionT = null, totalT = null;
        try {
            LlmResult result = ILlmClientService.chat(messages);
            reply = (result != null && result.firstText() != null)
                    ? result.firstText()
                    : (hasAnyContext
                    ? fallbackFromContext(request.getMessage(), allContexts)
                    : "I don't see any internal data for this question. Could you provide more details?");
            if (result != null) {
                promptT = result.getPromptTokens();
                completionT = result.getCompletionTokens();
                totalT = result.getTotalTokens();
            }
        } catch (Exception e) {
            log.error("LLM call failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            reply = hasAnyContext
                    ? fallbackFromContext(request.getMessage(), allContexts)
                    : "The assistant couldn't access internal data at the moment. Please try again later.";
        }

        // Log căn bản để debug chất lượng
        log.info("Q='{}' | ctx_db={} | ctx_client={} | any_ctx={} | ctx_words~={}",
                trimLog(request.getMessage(), 160),
                dbContexts.size(), clientCtx.size(), hasAnyContext,
                approxWordCount(contextBlock));

        return ChatResponse.builder()
                .reply(reply)
                .createdAt(OffsetDateTime.now())
                .promptTokens(promptT)
                .completionTokens(completionT)
                .totalTokens(totalT)
                .build();
    }

    // ---------- Helpers ----------

    private List<String> safeList(List<String> in) {
        return (in == null) ? Collections.emptyList() : in.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private List<String> dedupeKeepOrder(List<String> items) {
        return new ArrayList<>(new LinkedHashSet<>(items));
    }

    private String buildContextBlockWordLimited(List<String> contexts, int maxWords) {
        if (contexts == null || contexts.isEmpty()) return "(no internal context found)";
        StringBuilder sb = new StringBuilder();
        int idx = 1, words = 0;
        for (String c : contexts) {
            String line = ("Source #" + idx + ": " + c.trim()).replaceAll("\\s+", " ");
            int w = wordCount(line);
            if (words + w > maxWords) break;
            sb.append(line).append("\n");
            words += w;
            idx++;
        }
        String out = sb.toString().trim();
        return out.isBlank() ? "(no internal context found)" : out;
    }

    private String fallbackFromContext(String question, List<String> contexts) {
        // Trả lời tóm tắt 1–2 dòng dựa trên context đầu tiên để demo vẫn có output
        String first = contexts.get(0);
        return "Theo dữ liệu nội bộ: " + first +
                "\n(Ghi chú: câu trả lời này được tổng hợp trực tiếp từ context vì dịch vụ LLM đang lỗi/không phản hồi.)";
    }

    private int wordCount(String s) { return (int) Arrays.stream(s.split("\\s+")).filter(t -> !t.isBlank()).count(); }
    private int approxWordCount(String s) { return (s == null) ? 0 : wordCount(s); }
    private String trimLog(String s, int max) { return (s.length() <= max) ? s : s.substring(0, max) + "..."; }
}

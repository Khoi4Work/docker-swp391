package khoindn.swp391.be.app.model.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmResult {

    private List<Choice> choices = new ArrayList<>();
    private Usage usage;

    /** Trả về content đầu tiên KHÔNG rỗng (an toàn hơn). */
    public String firstText() {
        if (choices == null) return "";
        for (Choice c : choices) {
            if (c != null && c.message != null && c.message.content != null && !c.message.content.isBlank()) {
                return c.message.content;
            }
        }
        return "";
    }

    /** Helper: tạo LlmResult từ text + token cho trường hợp tự parse hoặc Fake client. */
    public static LlmResult from(String content, Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        LlmResult r = new LlmResult();

        Msg m = new Msg();
        m.role = "assistant";
        m.content = content;

        Choice ch = new Choice();
        ch.message = m;
        r.choices.add(ch);

        Usage u = new Usage();
        u.promptTokens = promptTokens;
        u.completionTokens = completionTokens;
        u.totalTokens = totalTokens;
        r.usage = u;

        return r;
    }

    // Convenience getters cho tokens (giữ tương thích với code hiện tại)
    public Integer getPromptTokens()     { return usage != null ? usage.getPromptTokens()     : null; }
    public Integer getCompletionTokens() { return usage != null ? usage.getCompletionTokens() : null; }
    public Integer getTotalTokens()      { return usage != null ? usage.getTotalTokens()      : null; }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Msg message;                 // {"role":"assistant","content":"..."}
        @JsonProperty("finish_reason") private String finishReason;
        private Integer index;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Msg {
        private String role;
        private String content;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("prompt_tokens")     private Integer promptTokens;
        @JsonProperty("completion_tokens") private Integer completionTokens;
        @JsonProperty("total_tokens")      private Integer totalTokens;
    }
}

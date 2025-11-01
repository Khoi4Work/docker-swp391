package khoindn.swp391.be.app.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;     // "system" | "user" | "assistant"
    private String content;

    public static Message system(String content) { return new Message("system", content); }
    public static Message user(String content)    { return new Message("user", content); }

    public Map<String, String> toMap() {
        return Map.of("role", role, "content", content);
    }
}

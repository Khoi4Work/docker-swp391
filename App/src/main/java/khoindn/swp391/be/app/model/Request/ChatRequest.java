package khoindn.swp391.be.app.model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple chat request payload.
 * Expand fields as needed (model, systemPrompt, temperature, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String message;
    private List<String> context; // optional: previous messages or keys; can be null
}

package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String reply;
    private OffsetDateTime createdAt;
    private Integer promptTokens;   // optional
    private Integer completionTokens; // optional
    private Integer totalTokens;    // optional
}

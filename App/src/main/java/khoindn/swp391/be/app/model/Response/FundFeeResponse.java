package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.pojo._enum.StatusFundDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundFeeResponse {
    private Integer fundDetailId;
    private Integer groupMemberId;
    private Integer userId;
    private String userName;
    private BigDecimal amount;
    private String monthYear;
    private StatusFundDetail status;
    private LocalDateTime createdAt;
    private Boolean isOverdue;
    private LocalDateTime dueDate;
}

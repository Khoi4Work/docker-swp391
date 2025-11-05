package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupFeeResponse {
    private Integer groupId;
    private String groupName;
    private String monthYear;
    private BigDecimal totalPending;
    private Integer pendingCount;
    private Integer paidCount;
    private List<FundFeeResponse> fees;
}

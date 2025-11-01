package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusFundDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "fund_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fund_detail_id")
    private Integer fundDetailId;
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    @Column(name = "transaction_type", length = 20, nullable = false)
    private String transactionType;
    @Column(name = "status", length = 20, nullable = false)
    private StatusFundDetail status = StatusFundDetail.PENDING;
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    private CommonFund commonFund;
    // relationship
    @ManyToOne
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMember groupMember;


}

package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusFundDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String transactionType = "FEE";
    @Column(name = "status", length = 20, nullable = false)
    private StatusFundDetail status = StatusFundDetail.PENDING;
    @Column(name = "month_year", length = 7, nullable = false)
    private String monthYear;
    @Column(name = "is_overdue", nullable = false)
    private Boolean isOverdue = false;

    @Column(name = "due_date")
    private LocalDateTime dueDate;


    // relationship
    @ManyToOne
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMember groupMember;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}

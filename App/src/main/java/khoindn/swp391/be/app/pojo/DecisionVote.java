package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusDecisionVote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionVote{

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "decision_name", unique = true)
    private String decisionName; // Maintenance, Insurance, Battery / Energy, Financial
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusDecisionVote status = StatusDecisionVote.PENDING;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    // Relationships
    @ManyToOne
    @JoinColumn(name = "created_by")
    private GroupMember createdBy;

    @OneToMany(mappedBy = "decisionVote")
    List<DecisionVoteDetail> decisionVoteDetails = new ArrayList<>();
}

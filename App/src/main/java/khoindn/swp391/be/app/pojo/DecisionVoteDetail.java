package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.OptionDecisionVoteDetail;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DecisionVoteDetail {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "optionVote")
    private OptionDecisionVoteDetail optionDecisionVote = OptionDecisionVoteDetail.ABSENT;

    @Column(name = "voted_at")
    private LocalDateTime votedAt = LocalDateTime.now();

    // Relationships
    @ManyToOne
    @JoinColumn(name = "voter_id")
    private GroupMember groupMember;

    @ManyToOne
    @JoinColumn(name = "decision")
    @JsonIgnore
    @ToString.Exclude
    DecisionVote decisionVote;
}

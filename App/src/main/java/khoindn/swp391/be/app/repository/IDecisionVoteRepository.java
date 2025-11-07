package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.DecisionVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDecisionVoteRepository extends JpaRepository<DecisionVote, Long> {
    DecisionVote getDecisionVoteById(Long id);

    List<DecisionVote> getDecisionVoteByCreatedBy_Group_GroupId(int createdByGroupGroupId);
}

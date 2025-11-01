package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.DecisionVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDecisionVoteRepository extends JpaRepository<DecisionVote, Long> {
    DecisionVote getDecisionVoteById(Long id);
}

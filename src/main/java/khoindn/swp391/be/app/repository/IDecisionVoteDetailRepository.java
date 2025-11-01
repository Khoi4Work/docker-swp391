package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.DecisionVote;
import khoindn.swp391.be.app.pojo.DecisionVoteDetail;
import khoindn.swp391.be.app.pojo.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDecisionVoteDetailRepository extends JpaRepository<DecisionVoteDetail, Long>
{
    DecisionVoteDetail getDecisionVoteDetailByGroupMember(GroupMember groupMember);

    DecisionVoteDetail getDecisionVoteDetailByGroupMemberAndDecisionVote_Id(GroupMember groupMember, Long decisionVoteId);

    List<DecisionVoteDetail> getAllByDecisionVote(DecisionVote decisionVote);
}

package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.pojo.DecisionVote;
import khoindn.swp391.be.app.pojo.DecisionVoteDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionVoteRes {
    DecisionVote creator;
    List<DecisionVoteDetail> voters;
}

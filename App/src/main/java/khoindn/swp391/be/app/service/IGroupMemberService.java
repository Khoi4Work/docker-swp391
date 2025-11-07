package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.DecisionVoteReq;
import khoindn.swp391.be.app.model.Request.LeaveGroupReq;
import khoindn.swp391.be.app.model.Response.AllGroupsOfMember;
import khoindn.swp391.be.app.model.Response.DecisionVoteRes;
import khoindn.swp391.be.app.model.Response.GroupMemberDetailRes;
import khoindn.swp391.be.app.pojo.*;

import java.util.List;

public interface IGroupMemberService {

    List<GroupMember> findAllByUsersId(int userId);

    List<Integer> getGroupIdsByUserId(int userId);

    List<GroupMember> getMembersByGroupId(int groupId);

    public List<GroupMemberDetailRes> getGroupMembersByGroupId(int groupId);

    GroupMember getGroupOwnerByGroupIdAndUserId(int groupId, int userId);

    List<AllGroupsOfMember> getAllGroupsOfMember(Users user);

    // ---------------------- NEW METHOD ----------------------
    GroupMember addMemberToGroup(int groupId, int userId, String roleInGroup, Float ownershipPercentage);


    DecisionVoteRes createDecision(DecisionVoteReq request, GroupMember gm);

    DecisionVote setDecision(int choice, long idDecision, int serviceId, GroupMember gm);

    DecisionVote checkAllVoters(DecisionVote vote, int groupId, int serviceId);

    DecisionVote getDecisionVoteById(long id);

    List<DecisionVoteDetail> getAllDecisionVoteDetailByDecisionVote(DecisionVote decisionVote);
}

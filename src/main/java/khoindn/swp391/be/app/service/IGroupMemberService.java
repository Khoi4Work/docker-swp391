package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.DecisionVoteReq;
import khoindn.swp391.be.app.model.Request.LeaveGroupReq;
import khoindn.swp391.be.app.model.Response.AllGroupsOfMember;
import khoindn.swp391.be.app.model.Response.GroupMemberDetailRes;
import khoindn.swp391.be.app.pojo.DecisionVote;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.RequestVehicleService;
import khoindn.swp391.be.app.pojo.Users;

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

    //member action

    GroupMember leaveGroup(LeaveGroupReq request);

    DecisionVote createDecision(DecisionVoteReq request, GroupMember gm);

    DecisionVote setDecision(int choice, long idDecision, GroupMember gm);

    RequestVehicleService requestVehicleService(int groupId, int serviceId);



}

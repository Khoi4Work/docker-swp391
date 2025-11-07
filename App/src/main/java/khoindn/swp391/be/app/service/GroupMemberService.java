package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.exception.exceptions.*;
import khoindn.swp391.be.app.model.Request.DecisionVoteReq;
import khoindn.swp391.be.app.model.Request.LeaveGroupReq;
import khoindn.swp391.be.app.model.Response.AllGroupsOfMember;
import khoindn.swp391.be.app.model.Response.DecisionVoteRes;
import khoindn.swp391.be.app.model.Response.GroupMemberDetailRes;
import khoindn.swp391.be.app.pojo.*;
import khoindn.swp391.be.app.pojo.RequestGroupService;
import khoindn.swp391.be.app.pojo._enum.OptionDecisionVoteDetail;
import khoindn.swp391.be.app.pojo._enum.StatusDecisionVote;
import khoindn.swp391.be.app.pojo._enum.StatusGroup;
import khoindn.swp391.be.app.pojo._enum.StatusGroupMember;
import khoindn.swp391.be.app.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupMemberService implements IGroupMemberService {

    @Autowired
    private IGroupMemberRepository iGroupMemberRepository;

    // ---------------------- NEW REPO INJECTION ----------------------
    @Autowired
    private IGroupRepository iGroupRepository;

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IDecisionVoteRepository iDecisionVoteRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IDecisionVoteDetailRepository iDecisionVoteDetailRepository;
    @Autowired
    private IVehicleService iVehicleService;


    // ---------------------- EXISTING CODE ----------------------
    @Override
    public List<GroupMember> findAllByUsersId(int userId) {
        return iGroupMemberRepository.findAllByUsersId(userId);
    }

    @Override
    public List<Integer> getGroupIdsByUserId(int userId) {
        List<GroupMember> groupMembers = iGroupMemberRepository.findAllByUsersId(userId);
        return groupMembers.stream()
                .map(gm -> gm.getGroup().getGroupId())
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupMember> getMembersByGroupId(int groupId) {
        return iGroupMemberRepository.findAllByGroup_GroupId(groupId);
    }

    @Override
    public List<GroupMemberDetailRes> getGroupMembersByGroupId(int groupId) {
        List<GroupMember> groupMembers = iGroupMemberRepository.findByGroup_GroupId(groupId);

        return groupMembers.stream()
                .map(gm -> {
                    GroupMemberDetailRes res = new GroupMemberDetailRes();
                    res.setUserId(gm.getUsers().getId());
                    res.setRoleInGroup(gm.getRoleInGroup());
                    res.setOwnershipPercentage(gm.getOwnershipPercentage());
                    res.setId(gm.getId());
                    res.setGroupId(gm.getGroup().getGroupId());
                    res.setHovaten(gm.getUsers().getHovaTen());
                    return res;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<AllGroupsOfMember> getAllGroupsOfMember(Users user) {
        List<GroupMember> gm = iGroupMemberRepository.findAllByUsersId(user.getId());


        List<AllGroupsOfMember> res = new ArrayList<>();
        for (GroupMember each : gm) {
            AllGroupsOfMember agm = modelMapper.map(each, AllGroupsOfMember.class);
            agm.setMembers(iGroupMemberRepository.findAllByGroup_GroupId(each.getGroup().getGroupId())
                    .stream()
                    .filter(groupMember ->
                            !user.getId().equals(groupMember.getUsers().getId()))
                    .toList());
            res.add(agm);
        }
        return res;
    }

    @Override
    public GroupMember getGroupOwnerByGroupIdAndUserId(int groupId, int userId) {
        return iGroupMemberRepository.findGroupMembersByUsers_IdAndGroup_GroupId(userId, groupId);
    }

    // ---------------------- NEW CODE: Add member to group ----------------------
    @Override
    @Transactional
    public GroupMember addMemberToGroup(int groupId, int userId, String roleInGroup, Float ownershipPercentage) {
        Group group = iGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("GROUP_NOT_FOUND"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        // Check duplicate
        iGroupMemberRepository.findByGroupAndUsers(group, user).ifPresent(gm -> {
            throw new IllegalStateException("ALREADY_IN_GROUP");
        });
        double addPct = ownershipPercentage == null ? 0.0 : ownershipPercentage.doubleValue();

// Khóa để tránh 2 request đồng thời vượt 100%
        iGroupMemberRepository.lockAllByGroupId(groupId);

// Lấy tổng hiện tại
        float currentTotal = iGroupMemberRepository.sumOwnershipByGroupId(groupId);

// Epsilon để tránh sai số số thực
        final float EPS = 0.0001f;
        if (currentTotal + addPct > 100.0f + EPS) {
            throw new IllegalStateException("OWNERSHIP_TOTAL_EXCEEDS_100");
        }
        GroupMember gm = new GroupMember();
        gm.setGroup(group);
        gm.setUsers(user);
        gm.setRoleInGroup((roleInGroup == null || roleInGroup.isBlank()) ? "MEMBER" : roleInGroup.trim());
        gm.setCreatedAt(LocalDateTime.now());
        gm.setOwnershipPercentage(ownershipPercentage == null ? 0f : ownershipPercentage);

        return iGroupMemberRepository.save(gm);
    }


    @Override
    public DecisionVoteRes createDecision(DecisionVoteReq request, GroupMember gm) {
        DecisionVoteRes res = new DecisionVoteRes();
        // map request to decisionVote
        DecisionVote createdDecisionVote = modelMapper.map(request, DecisionVote.class);
        createdDecisionVote.setEndedAt(LocalDateTime.now().plusDays(1));
        createdDecisionVote.setCreatedBy(gm);
        iDecisionVoteRepository.save(createdDecisionVote);

        res.setCreator(createdDecisionVote);

        Users user = authenticationService.getCurrentAccount();
        List<GroupMember> members = iGroupMemberRepository.findAllByGroup_GroupId(gm.getGroup().getGroupId())
                .stream()
                .filter(groupMember -> !groupMember.getUsers().getId().equals(user.getId()))
                .toList();


        for (GroupMember each : members) {
            DecisionVoteDetail voteDetail = new DecisionVoteDetail();
            voteDetail.setGroupMember(each);
            voteDetail.setDecisionVote(createdDecisionVote);
            iDecisionVoteDetailRepository.save(voteDetail);
        }

        res.setVoters(iDecisionVoteDetailRepository.getAllByDecisionVote(createdDecisionVote));

        return res;
    }

    @Override
    public DecisionVote setDecision(int choice, long idDecision, int serviceId, GroupMember gm) {
        DecisionVote vote = iDecisionVoteRepository.getDecisionVoteById(idDecision);
        if (vote == null) {
            throw new DecisionVoteNotFoundException("DECISION_NOT_FOUND_OR_DECISION_NOT_EXISTS");
        }

        DecisionVoteDetail voteDetail =
                iDecisionVoteDetailRepository.getDecisionVoteDetailByGroupMemberAndDecisionVote_Id(gm, idDecision);
        if (voteDetail == null) {
            throw new DecisionVoteDetailNotFoundException("VOTER_NOT_FOUND_OR_VOTER_NOT_EXISTS");
        }

        switch (choice) {
            case 0:
                voteDetail.setOptionDecisionVote(OptionDecisionVoteDetail.REJECTED);
                break;
            case 1:
                voteDetail.setOptionDecisionVote(OptionDecisionVoteDetail.ACCEPTED);
                break;
            default:
                throw new UndefinedChoiceException("Choice is invalid!");
        }

        //Mô hình này được áp dụng phổ biến trong các hợp tác xã, công ty cổ phần,
        // tổ hợp tác, và DAO (Decentralized Autonomous Organization) trong blockchain.
        //
        //Ở Việt Nam: Luật Doanh nghiệp 2020 – Điều 148 quy định
        // “Nghị quyết được thông qua nếu số cổ phần tán thành chiếm ít nhất 65% tổng số phiếu biểu quyết”
        // (có thể điều chỉnh tỷ lệ theo điều lệ).
        //
        //→ Tức là 65%–75% là mức hợp lý, tùy bạn chọn.

        // Lưu lại chi tiết vote
        iDecisionVoteDetailRepository.save(voteDetail);

        // check voters's vote and request if vote is accepted
        checkAllVoters(vote, gm.getGroup().getGroupId(), serviceId);



        return vote;
    }

    @Override
    public DecisionVote checkAllVoters(DecisionVote vote, int groupId, int serviceId) {
        // Lấy toàn bộ danh sách phiếu cho quyết định này
        List<DecisionVoteDetail> voteDetails = iDecisionVoteDetailRepository.getAllByDecisionVote(vote);


        if (LocalDateTime.now().isAfter(vote.getEndedAt()) ||
                voteDetails.stream().noneMatch(
                        v -> v.getOptionDecisionVote() == OptionDecisionVoteDetail.ABSENT)) {

            // Tính tổng tỉ lệ đồng ý và từ chối
            long totalAccepted = voteDetails.stream()
                    .filter(v -> v.getOptionDecisionVote() == OptionDecisionVoteDetail.ACCEPTED)
                    .count();

            long totalRejected = voteDetails.stream()
                    .filter(v -> v.getOptionDecisionVote() == OptionDecisionVoteDetail.REJECTED)
                    .count();


            if (voteDetails.stream()
                    .filter(decisionVoteDetail -> decisionVoteDetail.getVotedAt().isBefore(vote.getEndedAt()))
                    .anyMatch(v -> v.getOptionDecisionVote() == OptionDecisionVoteDetail.ABSENT)) {

                totalRejected += voteDetails
                        .stream()
                        .filter(decisionVoteDetail -> decisionVoteDetail.getVotedAt().isBefore(vote.getEndedAt()))
                        .filter(v -> v.getOptionDecisionVote() == OptionDecisionVoteDetail.ABSENT)
                        .count();

            }

            if (totalRejected >= totalAccepted) {
                vote.setStatus(StatusDecisionVote.REJECTED);
            } else {
                vote.setStatus(StatusDecisionVote.APPROVED);
                iVehicleService.requestVehicleService(groupId, serviceId);
            }
            iDecisionVoteRepository.save(vote);

        }
        return vote;
    }

    @Override
    public DecisionVote getDecisionVoteById(long id) {
        return iDecisionVoteRepository.getDecisionVoteById(id);
    }

    @Override
    public List<DecisionVoteDetail> getAllDecisionVoteDetailByDecisionVote(DecisionVote decisionVote) {
        return iDecisionVoteDetailRepository.findAllByDecisionVote(decisionVote);
    }


}

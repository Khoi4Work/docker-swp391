package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import khoindn.swp391.be.app.exception.exceptions.GroupMemberNotFoundException;
import khoindn.swp391.be.app.model.Request.AddMemberRequest;
import khoindn.swp391.be.app.model.Request.DecisionVoteReq;
import khoindn.swp391.be.app.model.Request.VotingRequest;
import khoindn.swp391.be.app.model.Response.GroupMemberDetailRes;
import khoindn.swp391.be.app.model.Response.GroupMemberResponse;
import khoindn.swp391.be.app.pojo.DecisionVote;
import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.service.AuthenticationService;
import khoindn.swp391.be.app.service.IGroupMemberService;
import khoindn.swp391.be.app.service.IGroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groupMember")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:8081")
public class GroupMemberController {

    @Autowired
    private IGroupMemberService iGroupMemberService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IGroupService iGroupService;
    @Autowired
    private AuthenticationService authenticationService;

    // ---------------------- EXISTING CODE ----------------------
    @GetMapping("/getByUserId")
    public ResponseEntity<List<GroupMember>> getGroupMembersByUserId(@RequestParam("userId") int userId) {
        List<GroupMember> groupMember = iGroupMemberService.findAllByUsersId(userId);
        return ResponseEntity.ok(groupMember);
    }

    @GetMapping("/getGroupIdsByUserId")
    public ResponseEntity<List<Integer>> getGroupIdsByUserId(@RequestParam("userId") int userId) {
        List<Integer> groupIds = iGroupMemberService.getGroupIdsByUserId(userId);
        return ResponseEntity.ok(groupIds);
    }

    // ---------------------- NEW CODE: Add member to group ----------------------
    @PostMapping("/add")
    public ResponseEntity<GroupMemberResponse> addMember(
            @RequestParam("groupId") int groupId,
            @Valid @RequestBody AddMemberRequest req) {

        GroupMember saved = iGroupMemberService.addMemberToGroup(
                groupId, req.getUserId(), req.getRoleInGroup(), req.getOwnershipPercentage());
        if (saved == null) {
            return ResponseEntity.badRequest().build();
        }
        GroupMemberResponse body = new GroupMemberResponse(
                saved.getId(),
                saved.getGroup(),
                saved.getUsers(),
                saved.getRoleInGroup(),
                saved.getStatus().name(),
                saved.getCreatedAt(),
                saved.getOwnershipPercentage()
        );
        if (body == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    @GetMapping("/members/{groupId}")
    public ResponseEntity getMembersByGroupId(@PathVariable int groupId) {
        Group group = iGroupService.getGroupById(groupId);
        List<GroupMemberResponse> allMembers = iGroupMemberService.getMembersByGroupId(groupId).stream()
                .map(groupMember -> modelMapper.map(groupMember, GroupMemberResponse.class))
                .toList();
        if (allMembers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No members found for groupId: " + group.getGroupName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(allMembers);
    }

    @PostMapping("/decision/group/{idGroup}")
    public ResponseEntity createDecision(@PathVariable int idGroup, @RequestBody @Valid DecisionVoteReq request) {
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        GroupMember gm = iGroupMemberService.getGroupOwnerByGroupIdAndUserId(user.getId(), idGroup);
        if (gm == null) {
            throw new GroupMemberNotFoundException("Member is not in Group!");
        }
        DecisionVote decisionVote = iGroupMemberService.createDecision(request, gm);
        if (decisionVote == null) {
            return ResponseEntity.status(500).body("INTERNAL SERVER ERROR");
        }
        return ResponseEntity.status(201).body(decisionVote);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupMemberDetailRes>> getGroupMembersByGroupId(@PathVariable int groupId) {
        List<GroupMemberDetailRes> members = iGroupMemberService.getGroupMembersByGroupId(groupId);
        return ResponseEntity.ok(members);
    }

    @PatchMapping("/decision")
    public ResponseEntity setDecision(@RequestBody VotingRequest votingRequest) {
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        GroupMember groupMember = iGroupMemberService.getGroupOwnerByGroupIdAndUserId(votingRequest.getGroupId(), user.getId());
        if (groupMember == null) {
            throw new GroupMemberNotFoundException("Member is not in Group!");
        }
        DecisionVote vote = iGroupMemberService.setDecision(
                votingRequest.getVote(),
                votingRequest.getDecisionId(),
                votingRequest.getServiceId(),
                groupMember);

        return ResponseEntity.status(200).body(vote);
    }
}

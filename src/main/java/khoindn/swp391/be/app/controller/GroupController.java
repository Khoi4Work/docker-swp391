package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import khoindn.swp391.be.app.exception.exceptions.GroupMemberNotFoundException;
import khoindn.swp391.be.app.model.Request.DecisionVoteReq;
import khoindn.swp391.be.app.model.Request.GroupCreateReq;
import khoindn.swp391.be.app.model.Request.GroupRequest;
import khoindn.swp391.be.app.model.Response.AllGroupsOfMember;
import khoindn.swp391.be.app.model.Response.RegisterVehicleRes;
import khoindn.swp391.be.app.pojo.DecisionVote;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.RequestVehicleService;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.service.AuthenticationService;
import khoindn.swp391.be.app.service.IGroupMemberService;
import khoindn.swp391.be.app.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@SecurityRequirement(name = "api")


public class GroupController {

    @Autowired
    private IGroupService iGroupService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IGroupMemberService iGroupMemberService;

    @PostMapping("/create")
    public ResponseEntity<RegisterVehicleRes> createGroup
            (@RequestBody @Valid GroupCreateReq request) {
        RegisterVehicleRes group = iGroupService.addMemberToGroupByContract(request);
        return ResponseEntity.status(201).body(group); // 201 Created
    }

    @GetMapping("/get/current")
    public ResponseEntity<List<AllGroupsOfMember>> getBelongedGroup() {
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<AllGroupsOfMember> groups = iGroupMemberService.getAllGroupsOfMember(user);
        return ResponseEntity.status(200).body(groups);
    }

    @PostMapping("/request")
    public ResponseEntity createRequestGroup(@Valid GroupRequest request) {
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            iGroupService.createRequestGroup(request, user);
        } catch (Exception e) {
            e.getMessage();
        }

        return ResponseEntity.status(201).body("Created Request Successfully");
    }

    @GetMapping("/service/{groupId}")
    public ResponseEntity getAllVehicleServiceByGroupId(@PathVariable int groupId) {
        return  ResponseEntity.status(200).body(iGroupService.getAllVehicleServiceByGroupId(groupId));
    }

}

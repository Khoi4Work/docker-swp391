package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import khoindn.swp391.be.app.model.Request.LeaveGroupReq;
import khoindn.swp391.be.app.model.Request.UpdateRequestGroup;
import khoindn.swp391.be.app.model.Response.ContractPendingRes;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.RequestGroupService;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@CrossOrigin(origins = "http://localhost:8081")
@SecurityRequirement(name = "api")
public class StaffController {
    @Autowired
    private IGroupService iGroupService;
    @Autowired
    private IRequestGroupService iRequestGroupService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IContractService iContractService;
    @Autowired
    private IStaffService iStaffService;

    // DELETE GROUP

    @DeleteMapping("/delete/group/{groupId}")
    public ResponseEntity deleteGroup(@PathVariable int groupId) {
        iGroupService.deleteGroup(groupId);
        return ResponseEntity.ok().body("Delete group successfully");
    }

    // LEAVE GROUP
    
    @PatchMapping("/leave/group")
    public ResponseEntity leaveGroup(LeaveGroupReq request) {
        Users staff = authenticationService.getCurrentAccount();
        if (!staff.getRole().getRoleName().equalsIgnoreCase("staff")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        GroupMember user_leaving = iStaffService.leaveGroup(request);
        return ResponseEntity.status(HttpStatus.OK).body(user_leaving);
    }

    // GET ALL REQUEST GROUP

    @GetMapping("/all/group/request")
    public ResponseEntity getAllRequestGroup() {
        List<RequestGroupService> res = iRequestGroupService.getAllRequestGroup();
        if (res.isEmpty()) {
            return ResponseEntity.status(204).body("No Content");
        }
        return ResponseEntity.status(200).body(res);
    }

    // UPDATE REQUEST GROUP

    @PatchMapping("/group/request")
    public ResponseEntity updateRequestGroup(@RequestBody @Valid UpdateRequestGroup update) {
        Users staff = authenticationService.getCurrentAccount();
        if (!staff.getRole().getRoleName().equalsIgnoreCase("staff")) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        iRequestGroupService.updateRequestGroup(update, staff);
        return ResponseEntity.status(200).body("Update successfully");
    }

    // 1. GET PENDING CONTRACTS

    @GetMapping("/contract/pending")
    public ResponseEntity getPendingContractRequests() {
        Users staff = authenticationService.getCurrentAccount();
        if (!staff.getRole().getRoleName().equalsIgnoreCase("staff")) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        List<ContractPendingRes> res = iContractService.getPendingContracts();
        if (res.isEmpty()) {
            return ResponseEntity.status(204).body("No Content");
        }
        return ResponseEntity.status(200).body(res);
    }

    // 2. APPROVE or REJECT CONTRACT AND SEND EMAIL RESULT TO CUSTOMER

    @PatchMapping("/contract/{contractId}/{decision}")
    public ResponseEntity verifyContract(@PathVariable int contractId, @PathVariable int decision) throws Exception {
        Users staff = authenticationService.getCurrentAccount();
        if (!staff.getRole().getRoleName().equalsIgnoreCase("staff")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            iContractService.verifyContract(contractId, decision);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(200).body("Verify successfully");
    }



}

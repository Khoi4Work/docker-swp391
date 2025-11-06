package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import khoindn.swp391.be.app.model.Request.ContractCreateReq;
import khoindn.swp391.be.app.model.Request.ContractDecisionReq;
import khoindn.swp391.be.app.model.Request.SendBulkEmailReq;
import khoindn.swp391.be.app.model.Response.ContractHistoryRes;
import khoindn.swp391.be.app.model.Response.RenderContractRes;
import khoindn.swp391.be.app.pojo.*;
import khoindn.swp391.be.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;


@RestController
@RequestMapping("/contract")
@SecurityRequirement(name = "api")

public class ContractController {

    @Autowired
    private IContractService iContractService;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IGroupMemberService iGroupMemberService;
    @Autowired
    private IVehicleService iVehicleService;
    @Autowired
    private IEmailService iEmailService;
    @Autowired
    private SupabaseService supabaseService;
    @Autowired
    private ISupabaseService iSupabaseService;

    // Lấy contract
    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractByContractId(@PathVariable int id) {
        Contract contract = iContractService.getContractByContractId(id);
        if (contract == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(contract);
    }

    // Tạo/Set contract
    @PostMapping(value = "/set", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContractSigner> setContract(@ModelAttribute @Valid ContractDecisionReq req)
            throws
            Exception {

        ContractSigner contractResult = iContractService.setContract(req);
        if (contractResult == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(contractResult);
    }



    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ContractSigner>> createContract(@ModelAttribute @Valid ContractCreateReq req)
            throws Exception {
        System.out.println(req);
        List<ContractSigner> contractResult = iContractService.createContract(req);
        if (contractResult == null) {
            throw new RuntimeException("Failed to create contract");
        }
        System.out.println(contractResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(contractResult);
    }

    @GetMapping("/user/current")
    public ResponseEntity<Contract> getContractsByUserCurrent() {
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Contract contract = iContractService.getContractByContractId(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(contract);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ContractHistoryRes>> getHistoryContractsByUser() {
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ContractHistoryRes> res = iContractService.getHistoryContractsByUser(user)
                .stream()
                .filter(contractHistory ->
                        contractHistory.getStatus().equalsIgnoreCase("activated"))
                .toList();
        if (res == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println(res);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/preview")
    public ResponseEntity renderContract(@RequestParam("contractId") int contractId) {
        //  Lấy người dùng hiện tại
        Users user = authenticationService.getCurrentAccount();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //  Người ký hợp đồng
        List<ContractSigner> contractSigners = iContractService.getAllContractSignersByContractId(contractId);
        if (contractSigners == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người ký hợp đồng.");
        }
        //  Lấy hợp đồng
        Contract contract = iContractService.getContractByContractId(contractId);
        if (contract == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hợp đồng.");
        }


        //  Lấy xe
        Vehicle vehicle = iVehicleService.findVehicleByGroupId(contract.getGroup().getGroupId());
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy xe thuộc nhóm này.");
        }

        //  Lấy danh sách thành viên nhóm
        List<GroupMember> allMembers = iGroupMemberService.getMembersByGroupId(contract.getGroup().getGroupId());
        if (allMembers == null || allMembers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không có thành viên trong nhóm.");
        }

        //  Xác định chủ sở hữu chính (tỷ lệ cao nhất)
        GroupMember ownerMember = allMembers.stream()
                .max(Comparator.comparing(GroupMember::getOwnershipPercentage))
                .orElse(null);
        if (ownerMember == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chủ sở hữu chính.");
        }

        //  Các đồng sở hữu khác
        List<GroupMember> coOwnerMembers = allMembers.stream()
                .filter(m -> !m.getUsers().getId().equals(ownerMember.getUsers().getId()))
                .toList();

        //set response
        RenderContractRes renderContractRes = new RenderContractRes();
        renderContractRes.setContracts(contractSigners);
        renderContractRes.setVehicle(vehicle);
        renderContractRes.setOwnerMember(ownerMember);
        renderContractRes.setCoOwnerMembers(coOwnerMembers);
        return ResponseEntity.status(200).body(renderContractRes);
    }

    @GetMapping("/file")
    public ResponseEntity getFileByName(String fileName) {
        return ResponseEntity.status(200).body(iSupabaseService.getFileUrl(fileName));
    }




}

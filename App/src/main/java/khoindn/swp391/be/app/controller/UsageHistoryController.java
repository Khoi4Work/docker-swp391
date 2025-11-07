package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.model.Response.UsageHistoryListResponse;
import khoindn.swp391.be.app.model.Response.UsageHistoryDetailResponse;
import khoindn.swp391.be.app.service.IVehicleUsageHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usage-history")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:8081")
public class UsageHistoryController {
    @Autowired
    private IVehicleUsageHistoryService iVehicleUsageHistoryService;


    @GetMapping("/booking/{userId}/{groupId}")
    public ResponseEntity<List<UsageHistoryListResponse>> getUsageHistoryList(
            @PathVariable int userId,
            @PathVariable int groupId) {

        List<UsageHistoryListResponse> history =
                iVehicleUsageHistoryService.getUsageHistoryList(userId, groupId);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/booking/{groupId}")
    public ResponseEntity<List<UsageHistoryListResponse>> getUsageByGroupId(
            @PathVariable int groupId) {
        List<UsageHistoryListResponse> history =
                iVehicleUsageHistoryService.getUsageHistoryListByGroupId(groupId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/booking/detail/{scheduleId}")
    public ResponseEntity<UsageHistoryDetailResponse> getUsageHistoryDetail(
            @PathVariable int scheduleId) {

        UsageHistoryDetailResponse detail =
                iVehicleUsageHistoryService.getUsageHistoryDetail(scheduleId);

        return ResponseEntity.ok(detail);
    }
}

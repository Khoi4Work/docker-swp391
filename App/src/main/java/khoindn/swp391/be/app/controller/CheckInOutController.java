package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.model.Request.CheckInRequest;
import khoindn.swp391.be.app.model.Request.CheckOutRequest;
import khoindn.swp391.be.app.model.Response.CheckInResponse;
import khoindn.swp391.be.app.model.Response.CheckOutResponse;
import khoindn.swp391.be.app.model.Response.ScheduleDetailResponse;
import khoindn.swp391.be.app.model.Response.ScheduleListItemResponse;
import khoindn.swp391.be.app.service.ICheckInService;
import khoindn.swp391.be.app.service.ICheckOutService;
import khoindn.swp391.be.app.service.ScheduleCheckInOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:8081")
public class CheckInOutController {
    @Autowired
    ICheckInService iCheckInService;
    @Autowired
    ICheckOutService iCheckOutService;
    @Autowired
    private ScheduleCheckInOutService scheduleCheckInOutService;

    @PostMapping("/checkIn/{scheduleId}")
    public ResponseEntity<CheckInResponse> checkIn(
            @PathVariable int scheduleId,
            @RequestBody CheckInRequest checkInReq) {

        CheckInResponse result = iCheckInService.processCheckIn(scheduleId, checkInReq);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/checkOut/{scheduleId}")
    public ResponseEntity<CheckOutResponse> checkOut(
            @PathVariable int scheduleId,
            @RequestBody CheckOutRequest CheckOutReq) {
        CheckOutResponse result = iCheckOutService.processCheckOut(scheduleId, CheckOutReq);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/schedules/group/{groupId}/booked")
    public ResponseEntity<List<ScheduleListItemResponse>> getBookedSchedules(@PathVariable int groupId) {
        List<ScheduleListItemResponse> schedules = scheduleCheckInOutService.getSchedulesByGroup(groupId);
        return ResponseEntity.ok(schedules);
    }
    @GetMapping("/detail/{scheduleId}")
    public ResponseEntity<ScheduleDetailResponse> getScheduleDetail(@PathVariable int scheduleId) {
        ScheduleDetailResponse detail = scheduleCheckInOutService.getScheduleDetail(scheduleId);
        return ResponseEntity.ok(detail);
    }


}

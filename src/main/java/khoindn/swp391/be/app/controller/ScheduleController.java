package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import khoindn.swp391.be.app.exception.exceptions.NoVehicleInGroupException;
import khoindn.swp391.be.app.model.Request.ScheduleReq;
import khoindn.swp391.be.app.model.Response.OverrideInfoRes;
import khoindn.swp391.be.app.model.Response.ScheduleRes;
import khoindn.swp391.be.app.model.Response.VehicleRes;
import khoindn.swp391.be.app.service.IScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:8081")

public class ScheduleController {

    @Autowired
    private IScheduleService scheduleService;


    @PostMapping("/register")
    public ResponseEntity<ScheduleRes> createSchedule(@Valid @RequestBody ScheduleReq req) {
        ScheduleRes res = scheduleService.createSchedule(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);

    }

//    @GetMapping("/all")
//    public ResponseEntity<List<ScheduleRes>> getAllSchedules() {
//
//        List<ScheduleRes> schedules = scheduleService.getAllSchedules();
//        return ResponseEntity.ok(schedules);
//    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ScheduleRes>> getSchedulesByGroupId(@PathVariable int groupId) {
        List<ScheduleRes> schedules = scheduleService.findByGroupMember_Group_GroupId(groupId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/vehicle")
    public ResponseEntity<List<VehicleRes>> getVehicleByGroupAndUser(
            @RequestParam int groupId,
            @RequestParam int userId) {
        List<VehicleRes> resList = scheduleService.getCarsByGroupIdAndUserId(groupId, userId);
        System.out.println("Vehicles found: " + resList.size());

        if (resList.isEmpty()) {
            throw new NoVehicleInGroupException("No vehicle in user's group");
        }

        return ResponseEntity.ok(resList);
    }

    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<Void> updateSchedule(@PathVariable int scheduleId,
                                               @RequestBody ScheduleReq req) {
        scheduleService.updateSchedule(req, scheduleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable int scheduleId) {
        scheduleService.cancelSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/override-count")
    public ResponseEntity<OverrideInfoRes> getOverrideCount(
            @RequestParam int userId,
            @RequestParam int groupId) {

        OverrideInfoRes result = scheduleService.getOverrideCountForUser(userId, groupId);
        return ResponseEntity.ok(result);
    }


//    @PutMapping("/{id}")
//    public ResponseEntity<ScheduleRes> updateSchedule(@PathVariable Integer id,
//                                                      @RequestBody ScheduleReq req) {
//        return ResponseEntity.notFound().build();
//    }


}


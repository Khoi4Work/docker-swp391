package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.ScheduleNotFoundException;
import khoindn.swp391.be.app.model.Response.UsageHistoryListResponse;
import khoindn.swp391.be.app.model.Response.UsageHistoryDetailResponse;
import khoindn.swp391.be.app.pojo.CheckIn;
import khoindn.swp391.be.app.pojo.CheckOut;
import khoindn.swp391.be.app.pojo.Schedule;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleUsageHistoryService implements IVehicleUsageHistoryService {

    @Autowired
    private IScheduleRepository iScheduleRepository;

    @Autowired
    private ICheckInRepository iCheckInRepository;

    @Autowired
    private ICheckOutRepository iCheckOutRepository;

    @Autowired
    private IVehicleRepository iVehicleRepository;

    @Override
    public List<UsageHistoryListResponse> getUsageHistoryList(int userId, int groupId) {
        // Get all schedules of this group
        List<Schedule> schedules = iScheduleRepository.findByGroupMember_Group_GroupId(groupId);

        // Filter by user and sort by date (newest first)
        return schedules.stream()
                .filter(s -> s.getGroupMember().getUsers().getId() == userId)
                .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsageHistoryListResponse> getUsageHistoryListByGroupId(int groupId) {
        List<Schedule> schedules = iScheduleRepository.findByGroupMember_Group_GroupId(groupId);

        // Filter by user and sort by date (newest first)
        return schedules.stream()
                .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsageHistoryDetailResponse getUsageHistoryDetail(int scheduleId) {
        Schedule schedule = iScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException(
                        "Schedule không tồn tại với ID: " + scheduleId
                ));

        return convertToDetailResponse(schedule);
    }

    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================

    private UsageHistoryListResponse convertToListResponse(Schedule schedule) {
        UsageHistoryListResponse res = new UsageHistoryListResponse();

        // Basic info
        res.setScheduleId(schedule.getScheduleId());
        res.setDate(schedule.getStartTime().toLocalDate().toString());
        res.setTimeRange(formatTimeRange(schedule.getStartTime(), schedule.getEndTime()));

        // Vehicle
        Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
        if (vehicle != null) {
            res.setVehicleName(vehicle.getBrand() + " " + vehicle.getModel());
        }

        // User
        Users user = schedule.getGroupMember().getUsers();
        if (user != null) {
            res.setUserName(user.getHovaTen());
        }

        // Check-in/out status
        CheckIn checkIn = iCheckInRepository.findByScheduleScheduleId(schedule.getScheduleId());
        CheckOut checkOut = iCheckOutRepository.findByScheduleScheduleId(schedule.getScheduleId());

        res.setHasCheckIn(checkIn != null);
        res.setHasCheckOut(checkOut != null);

        return res;
    }

    private UsageHistoryDetailResponse convertToDetailResponse(Schedule schedule) {
        UsageHistoryDetailResponse res = new UsageHistoryDetailResponse();

        // Basic info
        res.setScheduleId(schedule.getScheduleId());
        res.setDate(schedule.getStartTime().toLocalDate().toString());

        // Vehicle
        Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
        if (vehicle != null) {
            res.setVehicleName(vehicle.getBrand() + " " + vehicle.getModel());
        }

        // User
        Users user = schedule.getGroupMember().getUsers();
        if (user != null) {
            res.setUserName(user.getHovaTen());
        }

        // Check-in info
        CheckIn checkIn = iCheckInRepository.findByScheduleScheduleId(schedule.getScheduleId());
        if (checkIn != null) {
            res.setCheckInTime(checkIn.getCheckInTime());
            res.setCheckInCondition(checkIn.getCondition());
            res.setCheckInNotes(checkIn.getNotes());
            res.setCheckInImages(checkIn.getImages());
        }

        // Check-out info
        CheckOut checkOut = iCheckOutRepository.findByScheduleScheduleId(schedule.getScheduleId());
        if (checkOut != null) {
            res.setCheckOutTime(checkOut.getCheckOutTime());
            res.setCheckOutCondition(checkOut.getCondition());
            res.setCheckOutNotes(checkOut.getNotes());
            res.setCheckOutImages(checkOut.getImages());
        }

        return res;
    }

    private String formatTimeRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return start.format(formatter) + " - " + end.format(formatter);
    }
}

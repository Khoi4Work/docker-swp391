package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.ScheduleNotFoundException;
import khoindn.swp391.be.app.model.Response.CheckInDetailResponse;
import khoindn.swp391.be.app.model.Response.CheckOutDetailResponse;
import khoindn.swp391.be.app.model.Response.ScheduleDetailResponse;
import khoindn.swp391.be.app.model.Response.ScheduleListItemResponse;
import khoindn.swp391.be.app.pojo.CheckIn;
import khoindn.swp391.be.app.pojo.CheckOut;
import khoindn.swp391.be.app.pojo.Schedule;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.pojo._enum.StatusSchedule;
import khoindn.swp391.be.app.repository.ICheckInRepository;
import khoindn.swp391.be.app.repository.ICheckOutRepository;
import khoindn.swp391.be.app.repository.IScheduleRepository;
import khoindn.swp391.be.app.repository.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleCheckInOutService implements IScheduleCheckInOutService {
    @Autowired
    private IScheduleRepository iScheduleRepository;
    @Autowired
    private ICheckInRepository iCheckInRepository;
    @Autowired
    private ICheckOutRepository iCheckOutRepository;
    @Autowired
    private IVehicleRepository iVehicleRepository;

    public List<ScheduleListItemResponse> getSchedulesByGroup(int groupId) {
        List<Schedule> schedules = iScheduleRepository
                .findByGroupMember_Group_GroupIdAndStatus(groupId, StatusSchedule.BOOKED);

        return schedules.stream()
                .map(schedule -> {
                    ScheduleListItemResponse res = new ScheduleListItemResponse();
                    res.setScheduleId(schedule.getScheduleId());
                    res.setStartTime(schedule.getStartTime());
                    res.setEndTime(schedule.getEndTime());

                    // Vehicle info
                    Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
                    if (vehicle != null) {
                        res.setVehicleName(vehicle.getBrand() + " " + vehicle.getModel());
                        res.setVehiclePlate(vehicle.getPlateNo());
                    }

                    // User info
                    if (schedule.getGroupMember().getUsers() != null) {
                        res.setUserName(schedule.getGroupMember().getUsers().getHovaTen());
                    }

                    // Check-in status
                    CheckIn checkIn = iCheckInRepository.findByScheduleScheduleId(schedule.getScheduleId());
                    res.setHasCheckIn(checkIn != null);
                    if (checkIn != null) {
                        res.setCheckInTime(checkIn.getCheckInTime());
                    }

                    // Check-out status
                    CheckOut checkOut = iCheckOutRepository.findByScheduleScheduleId((schedule.getScheduleId()));
                    res.setHasCheckOut(checkOut != null);
                    if (checkOut != null) {
                        res.setCheckOutTime(checkOut.getCheckOutTime());
                    }

                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDetailResponse getScheduleDetail(int scheduleId) {
        // Find schedule
        Schedule schedule = iScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException(
                        "Schedule không tồn tại với ID: " + scheduleId
                ));

        // Create response
        ScheduleDetailResponse response = new ScheduleDetailResponse();
        response.setScheduleId(schedule.getScheduleId());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        response.setScheduleStatus(schedule.getStatus());

        // Set vehicle info
        Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
        if (vehicle != null) {
            response.setVehicleName(vehicle.getBrand() + " " + vehicle.getModel());
            response.setVehiclePlate(vehicle.getPlateNo());
        }

        // Set user info
        if (schedule.getGroupMember().getUsers() != null) {
            response.setUserName(schedule.getGroupMember().getUsers().getHovaTen());
        }

        // Get check-in info
        CheckIn checkIn = iCheckInRepository.findByScheduleScheduleId(scheduleId);
        if (checkIn != null) {
            CheckInDetailResponse checkInDetail = new CheckInDetailResponse();
            checkInDetail.setCheckInId(checkIn.getCheckInId());
            checkInDetail.setCheckInTime(checkIn.getCheckInTime());
            checkInDetail.setCondition(checkIn.getCondition());
            checkInDetail.setNotes(checkIn.getNotes());
            checkInDetail.setImages(checkIn.getImages());
            response.setCheckIn(checkInDetail);
        }

        // Get check-out info
        CheckOut checkOut = iCheckOutRepository.findByScheduleScheduleId(scheduleId);
        if (checkOut != null) {
            CheckOutDetailResponse checkOutDetail = new CheckOutDetailResponse();
            checkOutDetail.setCheckOutId(checkOut.getCheckOutId());
            checkOutDetail.setCheckOutTime(checkOut.getCheckOutTime());
            checkOutDetail.setCondition(checkOut.getCondition());
            checkOutDetail.setNotes(checkOut.getNotes());
            checkOutDetail.setImages(checkOut.getImages());
            response.setCheckOut(checkOutDetail);
        }

        return response;
    }
}


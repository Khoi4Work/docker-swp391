package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.*;
import khoindn.swp391.be.app.model.Request.CheckOutRequest;
import khoindn.swp391.be.app.model.Response.CheckInResponse;
import khoindn.swp391.be.app.model.Response.CheckOutResponse;
import khoindn.swp391.be.app.pojo.CheckIn;
import khoindn.swp391.be.app.pojo.CheckOut;
import khoindn.swp391.be.app.pojo.Schedule;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.pojo._enum.StatusSchedule;
import khoindn.swp391.be.app.repository.ICheckInRepository;
import khoindn.swp391.be.app.repository.ICheckOutRepository;
import khoindn.swp391.be.app.repository.IScheduleRepository;
import khoindn.swp391.be.app.repository.IVehicleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckOutService implements ICheckOutService {
    @Autowired
    IScheduleRepository iScheduleRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ICheckOutRepository iCheckOutRepository;
    @Autowired
    IVehicleRepository iVehicleRepository;
    @Autowired
    ICheckInRepository iCheckInRepository;

    @Override
    public CheckOutResponse processCheckOut(int scheduleId, CheckOutRequest req) {
        //validate schedule exist
        Schedule schedule = iScheduleRepository.findById(scheduleId).get();
        if (schedule == null) {
            throw new ScheduleNotFoundException(
                    "Schedule không tồn tại với ID: " + scheduleId
            );
        }
        // check duplicate check out ID
        if (iCheckOutRepository.existsBySchedule_ScheduleId(scheduleId)) {
            throw new AlreadyCheckedOutException(
                    "Schedule này đã được check-out rồi. Không thể check-out lần nữa."
            );
        }
        // check userId to use check in or check out
        int scheduleOwnerId = schedule.getGroupMember().getUsers().getId();
        if (scheduleOwnerId != req.getUserId()) {
            throw new UnauthorizedAccessException(
                    String.format(
                            "Bạn không có quyền check-out schedule này. " +
                                    "Schedule thuộc về user ID: %d, nhưng bạn đang thao tác với user ID: %d",
                            scheduleOwnerId, req.getUserId()
                    )
            );
        }
        if (!schedule.getStatus().equals(StatusSchedule.BOOKED)) {
            throw new InvalidScheduleStatusException(
                    "Schedule không ở trạng thái booked. "
            );
        }
        // Check if checked in
        CheckIn checkIn = iCheckInRepository.findByScheduleScheduleId(scheduleId);
        if (checkIn == null) {
            throw new CheckInNotFoundException(
                    "Chưa check-in cho Schedule ID: " + scheduleId
            );
        }
        CheckOut checkOut = new CheckOut();
        checkOut.setSchedule(schedule);
        checkOut.setCondition(req.getCondition());
        checkOut.setNotes(req.getNotes());
        checkOut.setImages(req.getImages());
        checkOut.setCheckOutTime(LocalDateTime.now());
        CheckOut saved = iCheckOutRepository.save(checkOut);
        // response
        CheckOutResponse res = modelMapper.map(saved, CheckOutResponse.class);
        res.setCheckOutId(saved.getCheckOutId());
        res.setScheduleId(scheduleId);
        res.setUserId(req.getUserId());
        res.setCheckOutTime(saved.getCheckOutTime());
        // check in
        res.setCheckInId(checkIn.getCheckInId());
        res.setCheckInTime(checkIn.getCheckInTime());
        res.setCheckInCondition(checkIn.getCondition());
        // schedule

        res.setScheduleStartTime(schedule.getStartTime());
        res.setScheduleEndTime(schedule.getEndTime());
        res.setScheduleStatus(schedule.getStatus().name());
        // vehicle
        Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
        if (vehicle != null) {
            res.setVehicleName(vehicle.getBrand() + " " + vehicle.getModel());
        }
        // user
        if (schedule.getGroupMember().getUsers() != null) {
            res.setUserName(schedule.getGroupMember().getUsers().getHovaTen());
        }

        return res;

    }
}

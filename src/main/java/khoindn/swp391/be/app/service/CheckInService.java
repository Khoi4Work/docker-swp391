package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.AlreadyCheckedInException;
import khoindn.swp391.be.app.exception.exceptions.InvalidScheduleStatusException;
import khoindn.swp391.be.app.exception.exceptions.ScheduleNotFoundException;
import khoindn.swp391.be.app.exception.exceptions.UnauthorizedAccessException;
import khoindn.swp391.be.app.model.Request.CheckInRequest;
import khoindn.swp391.be.app.model.Response.CheckInResponse;
import khoindn.swp391.be.app.pojo.CheckIn;
import khoindn.swp391.be.app.pojo.Schedule;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.pojo._enum.StatusSchedule;
import khoindn.swp391.be.app.repository.ICheckInRepository;
import khoindn.swp391.be.app.repository.IScheduleRepository;
import khoindn.swp391.be.app.repository.IVehicleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service

public class CheckInService implements ICheckInService {
    @Autowired
    ICheckInRepository iCheckInRepository;
    @Autowired
    IScheduleRepository iScheduleRepository;
    @Autowired
    IVehicleRepository iVehicleRepository;
    @Autowired
    ModelMapper modelMapper;


    @Override
    public CheckInResponse processCheckIn(int scheduleId, CheckInRequest req) {
        //validate schedule exist
        Schedule schedule = iScheduleRepository.findById(scheduleId).get();
        if (schedule == null) {
            throw new ScheduleNotFoundException(
                    "Schedule không tồn tại với ID: " + scheduleId
            );
        }
        // check duplicate check id ID
        if (iCheckInRepository.existsBySchedule_ScheduleId(scheduleId)) {
            throw new AlreadyCheckedInException(
                    "Schedule này đã được check-in rồi. Không thể check-in lần nữa."
            );
        }
        // check userId to use check in or check out
        int scheduleOwnerId = schedule.getGroupMember().getUsers().getId();
        if (scheduleOwnerId != req.getUserId()) {
            throw new UnauthorizedAccessException(
                    String.format(
                            "Bạn không có quyền check-in schedule này. " +
                                    "Schedule thuộc về user ID: %d, nhưng bạn đang thao tác với user ID: %d",
                            scheduleOwnerId, req.getUserId()
                    )
            );
        }
        if (!schedule.getStatus().equals(StatusSchedule.BOOKED)) {
            throw new InvalidScheduleStatusException(
                    "Schedule không ở trạng thái booked . "
            );
        }
        CheckIn checkIn = new CheckIn();
        checkIn.setSchedule(schedule);
        checkIn.setCondition(req.getCondition());
        checkIn.setNotes(req.getNotes());
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setImages(req.getImages());
        CheckIn savedCheckIn = iCheckInRepository.save(checkIn);
        // response
        CheckInResponse response = modelMapper.map(savedCheckIn, CheckInResponse.class);
        response.setCheckInId(savedCheckIn.getCheckInId());
        response.setScheduleId(scheduleId);
        response.setUserId(req.getUserId());
        response.setCheckInDate(savedCheckIn.getCheckInTime());
        // set schedule info
        response.setScheduleStartTime(schedule.getStartTime());
        response.setScheduleEndTime(schedule.getEndTime());
        response.setScheduleStatus(schedule.getStatus().name());
        // set vehicle info
        Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
        response.setVehicleName(vehicle.getBrand() + " " + vehicle.getModel());
        // set user
        if (schedule.getGroupMember().getGroup() != null) {
            response.setUserName(schedule.getGroupMember().getUsers().getHovaTen());
        }
        return response;
    }
}

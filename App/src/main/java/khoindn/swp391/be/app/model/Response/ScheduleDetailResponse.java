package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.pojo._enum.StatusSchedule;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDetailResponse {
    private int scheduleId;
    private String vehicleName;
    private String vehiclePlate;
    private String userName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private StatusSchedule scheduleStatus;

    // Check-in info
    private CheckInDetailResponse checkIn;

    // Check-out info
    private CheckOutDetailResponse checkOut;
}

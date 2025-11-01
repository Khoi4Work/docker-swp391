package khoindn.swp391.be.app.model.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class ScheduleListItemResponse {
    // Schedule info
    private int scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Vehicle info
    private String vehicleName;
    private String vehiclePlate;

    // User info
    private String userName;

    // Check-in/out status
    private boolean hasCheckIn;
    private boolean hasCheckOut;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}

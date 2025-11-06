package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {
    private int checkInId;
    private int scheduleId;
    private int userId;
    private LocalDateTime checkInDate;
    private String condition;
    private String notes;
    private String images;
    private String vehicleName;
    private String userName;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
    private String scheduleStatus;
}

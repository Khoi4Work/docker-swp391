package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private int checkOutId;
    private int scheduleId;
    private int userId;
    // check in
    private LocalDateTime checkInTime;
    private String checkInCondition;
    private int checkInId;

    // check out
    private LocalDateTime checkOutTime;
    private String checkOutCondition;
    private String checkOutNotes;
    private String checkOutImages;
    // vehicle anda schedule
    private String vehicleName;
    private String userName;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
    private String scheduleStatus;
}

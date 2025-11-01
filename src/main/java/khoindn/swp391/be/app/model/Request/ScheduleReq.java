package khoindn.swp391.be.app.model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleReq {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    private int groupId;
    private int userId;
    private int vehicleId;
}

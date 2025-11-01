package khoindn.swp391.be.app.model.Response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UsageHistoryDetailResponse {
    private int scheduleId;
    private String date;
    private String vehicleName;
    private String userName;

    // Check-in
    private LocalDateTime checkInTime;
    private String checkInCondition;
    private String checkInNotes;
    private List<String> checkInImages;

    // Check-out
    private LocalDateTime checkOutTime;
    private String checkOutCondition;
    private String checkOutNotes;
    private List<String> checkOutImages;
}

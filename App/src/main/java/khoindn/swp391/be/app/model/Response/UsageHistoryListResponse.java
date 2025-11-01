package khoindn.swp391.be.app.model.Response;

import lombok.Data;

@Data
public class UsageHistoryListResponse {
    private int scheduleId;
    private String date;
    private String vehicleName;
    private String userName;
    private String timeRange;

    private boolean hasCheckIn;
    private boolean hasCheckOut;


}

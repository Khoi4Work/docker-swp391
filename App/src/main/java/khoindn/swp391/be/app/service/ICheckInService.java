package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.CheckInRequest;
import khoindn.swp391.be.app.model.Response.CheckInResponse;

public interface ICheckInService {
    public CheckInResponse processCheckIn(int scheduleId, CheckInRequest req);
}

package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.ScheduleReq;
import khoindn.swp391.be.app.model.Response.OverrideInfoRes;
import khoindn.swp391.be.app.model.Response.ScheduleRes;
import khoindn.swp391.be.app.model.Response.VehicleRes;
import khoindn.swp391.be.app.pojo.Schedule;
import khoindn.swp391.be.app.pojo.Vehicle;

import java.util.List;
import java.util.Map;

public interface IScheduleService {
    ScheduleRes createSchedule(ScheduleReq req);

    List<ScheduleRes> getAllSchedules();

    List<VehicleRes> getCarsByGroupIdAndUserId(int groupId, int userId);  // Sửa return type

    public void updateSchedule(ScheduleReq req, int scheduleId);

    void cancelSchedule(int scheduleId);
    List<ScheduleRes> findByGroupMember_Group_GroupId(int groupId);
    OverrideInfoRes getOverrideCountForUser(int userId, int groupId);


}

package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Response.ScheduleDetailResponse;
import khoindn.swp391.be.app.model.Response.ScheduleListItemResponse;

import java.util.List;

public interface IScheduleCheckInOutService {
    public List<ScheduleListItemResponse> getSchedulesByGroup(int groupId);
    ScheduleDetailResponse getScheduleDetail(int scheduleId);
}

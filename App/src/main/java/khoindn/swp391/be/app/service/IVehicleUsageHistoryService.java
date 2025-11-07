package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Response.UsageHistoryListResponse;
import khoindn.swp391.be.app.model.Response.UsageHistoryDetailResponse;
import java.util.List;

public interface IVehicleUsageHistoryService {
    List<UsageHistoryListResponse> getUsageHistoryList(int userId, int groupId);
    List<UsageHistoryListResponse> getUsageHistoryListByGroupId( int groupId);

    UsageHistoryDetailResponse getUsageHistoryDetail(int scheduleId);
}

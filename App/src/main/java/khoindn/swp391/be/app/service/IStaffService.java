package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.LeaveGroupReq;
import khoindn.swp391.be.app.pojo.GroupMember;

public interface IStaffService {
    GroupMember leaveGroup(LeaveGroupReq request);
}

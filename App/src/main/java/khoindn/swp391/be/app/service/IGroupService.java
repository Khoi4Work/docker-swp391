package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.GroupCreateReq;
import khoindn.swp391.be.app.model.Request.GroupRequest;
import khoindn.swp391.be.app.model.Response.RegisterVehicleRes;
import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.RequestVehicleService;
import khoindn.swp391.be.app.pojo.Users;

public interface IGroupService {
    public RegisterVehicleRes addMemberToGroupByContract(GroupCreateReq request);

    public void deleteGroup(int groupId);

    public void createRequestGroup(GroupRequest request, Users user);

    public Group getGroupById(int groupId);

    RequestVehicleService getAllVehicleServiceByGroupId( int groupId);
}

package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.RequestGroupNotFoundException;
import khoindn.swp391.be.app.model.Request.LeaveGroupReq;
import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.RequestGroupService;
import khoindn.swp391.be.app.pojo._enum.StatusGroup;
import khoindn.swp391.be.app.pojo._enum.StatusGroupMember;
import khoindn.swp391.be.app.repository.IGroupMemberRepository;
import khoindn.swp391.be.app.repository.IGroupRepository;
import khoindn.swp391.be.app.repository.IRequestGroupServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffService implements IStaffService{

    @Autowired
    IRequestGroupServiceRepository iRequestGroupServiceRepository;
    @Autowired
    private IGroupMemberRepository iGroupMemberRepository;
    @Autowired
    private IGroupRepository iGroupRepository;

    @Override
    public GroupMember leaveGroup(LeaveGroupReq request) {
        RequestGroupService requestProcessing = iRequestGroupServiceRepository.findRequestGroupById(request.getRequestId());
        if (requestProcessing == null) {
            throw new RequestGroupNotFoundException("REQUEST_NOT_FOUND");
        }
        // Update status of GroupMember
        GroupMember user_leaving = requestProcessing.getGroupMember();
        user_leaving.setStatus(StatusGroupMember.LEAVED);
        iGroupMemberRepository.save(user_leaving);
        // Update status of Group
        Group group = user_leaving.getGroup();
        group.setStatus(StatusGroup.INACTIVE);
        iGroupRepository.save(group);
        return user_leaving;
    }
}

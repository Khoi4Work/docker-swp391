package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.UpdateRequestGroup;
import khoindn.swp391.be.app.pojo.RequestGroupService;
import khoindn.swp391.be.app.pojo.Users;

import java.util.List;

public interface IRequestGroupService {

    public List<RequestGroupService> getAllRequestGroup();

    public void updateRequestGroup(UpdateRequestGroup update, Users staff);


}

package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.RequestVehicleService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRequestVehicleServiceRepository extends JpaRepository<RequestVehicleService,Long> {
    RequestVehicleService getAllByGroupMember_Group(Group groupMemberGroup);
}

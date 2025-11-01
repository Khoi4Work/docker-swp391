package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGroupRepository extends JpaRepository<Group, Integer> {
    Group findGroupByGroupId(int groupId);
}

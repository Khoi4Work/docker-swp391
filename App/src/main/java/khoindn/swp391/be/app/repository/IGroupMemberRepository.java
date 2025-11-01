package khoindn.swp391.be.app.repository;

import jakarta.persistence.LockModeType;
import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IGroupMemberRepository extends JpaRepository<GroupMember, Integer> {
    Optional<GroupMember> findByGroupAndUsers(Group group, Users users);

    List<GroupMember> findByGroup_GroupId(int groupId);

    List<GroupMember> findAllByUsersId(int userId);

    GroupMember findByGroupGroupIdAndUsersId(int groupId, int userId);

    GroupMember findGroupMembersByUsers(Users users);

    List<GroupMember> findAllByGroup_GroupId(int groupGroupId);

    GroupMember findGroupMembersByUsers_IdAndGroup_GroupId(Integer usersId, int groupGroupId);

    // ✅ TÍNH TỔNG OWNERSHIP CỦA 1 GROUP
    @Query("""
                select coalesce(sum(gm.ownershipPercentage), 0)
                from GroupMember gm
                where gm.group.groupId = :groupId
            """)
    Float sumOwnershipByGroupId(@Param("groupId") int groupId);

    // KHÓA BI QUAN TẤT CẢ BẢN GHI CỦA GROUP TRƯỚC KHI CỘNG DỒN
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select gm from GroupMember gm where gm.group.groupId = :groupId")
    List<GroupMember> lockAllByGroupId(@Param("groupId") int groupId);
}

package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRoleRepository extends JpaRepository<UserRole, Integer> {
    public UserRole findByRoleName(String rolename);

    boolean existsByRoleName(String roleName);

    UserRole findUserRoleByRoleId(int roleId);

    boolean existsUserRoleByRoleId(int roleId);
}

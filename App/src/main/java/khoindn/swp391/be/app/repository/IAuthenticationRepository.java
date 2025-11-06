package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.UserRole;
import khoindn.swp391.be.app.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAuthenticationRepository extends JpaRepository<Users, Integer> {
    Users findUsersByEmail(String email);

    Users findUserById(int id);

    boolean existsByEmail(String email);

    boolean existsByCccd(String cccd);

    boolean existsByGplx(String gplx);

    boolean existsByPhone(String phone);

    boolean existsByRole(UserRole role);

    boolean existsByRole_RoleId(int roleRoleId);
}

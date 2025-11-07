package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<Users, Integer> {

    public Users findByEmail(String email);

    Users findUsersById(Integer id);

    List<Users> findAllByRole_RoleId(Integer roleId);

    Users findByIdAndRole_RoleId(Integer id, Integer roleId);

    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
    boolean existsByPhone(String phone);
    boolean existsByGplx(String gplx);
}
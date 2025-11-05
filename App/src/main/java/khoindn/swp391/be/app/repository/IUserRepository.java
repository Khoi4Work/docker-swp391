package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
// Không import Optional

@Repository
public interface IUserRepository extends JpaRepository<Users, Integer> {

    // --- HÀM CŨ CỦA BẠN (CHO SPRING SECURITY) ---
    public Users findByEmail(String email);

    // --- HÀM CŨ CỦA BẠN ---
    Users findUsersById(Integer id);


    // --- CÁC HÀM MỚI (ĐÃ SỬA LỖI & BỎ OPTIONAL) ---

    // Sửa lỗi: "findAllByRoleId" -> "findAllByRole_RoleId"
    List<Users> findAllByRole_RoleId(Integer roleId);

    // Sửa lỗi: "findByIdAndRoleId" -> "findByIdAndRole_RoleId"
    Users findByIdAndRole_RoleId(Integer id, Integer roleId);

    // (Các hàm kiểm tra)
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
    boolean existsByPhone(String phone);
}
// trong /service/StaffManagementService.java
package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.CreateStaffRequest;
import khoindn.swp391.be.app.model.Response.StaffResponse;
import khoindn.swp391.be.app.model.Request.UpdateStaffRequest;
import khoindn.swp391.be.app.pojo.UserRole;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.repository.IUserRepository;
import khoindn.swp391.be.app.repository.IUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffManagementService implements IStaffManagementService {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Integer STAFF_ROLE_ID = 4;

    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }
        if (userRepository.existsByCccd(request.getCccd())) {
            throw new RuntimeException("Lỗi: CCCD đã tồn tại!");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Lỗi: Số điện thoại đã tồn tại!");
        }

        UserRole staffRole = userRoleRepository.findUserRoleByRoleId(STAFF_ROLE_ID);
        if (staffRole == null) {
            throw new RuntimeException("Lỗi hệ thống: Không tìm thấy Role ID " + STAFF_ROLE_ID);
        }

        Users newStaff = new Users();
        newStaff.setHovaTen(request.getHovaTen());
        newStaff.setEmail(request.getEmail());
        newStaff.setPassword(passwordEncoder.encode(request.getPassword()));
        newStaff.setCccd(request.getCccd());
        newStaff.setPhone(request.getPhone());
        newStaff.setRole(staffRole);

        Users savedStaff = userRepository.save(newStaff);
        return mapEntityToResponse(savedStaff);
    }

    @Override
    public StaffResponse getStaffById(Integer staffId) {
        // Đã sửa: Dùng hàm đã fix lỗi và check null
        Users staff = userRepository.findByIdAndRole_RoleId(staffId, STAFF_ROLE_ID);
        if (staff == null) {
            throw new RuntimeException("Không tìm thấy Staff với ID: " + staffId);
        }
        return mapEntityToResponse(staff);
    }

    @Override
    public List<StaffResponse> getAllStaff() {
        // Đã sửa: Dùng hàm đã fix lỗi
        List<Users> staffList = userRepository.findAllByRole_RoleId(STAFF_ROLE_ID);
        return staffList.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponse updateStaff(Integer staffId, UpdateStaffRequest request) {
        // Đã sửa: Dùng hàm đã fix lỗi và check null
        Users existingStaff = userRepository.findByIdAndRole_RoleId(staffId, STAFF_ROLE_ID);
        if (existingStaff == null) {
            throw new RuntimeException("Không tìm thấy Staff với ID: " + staffId);
        }

        if (request.getHovaTen() != null && !request.getHovaTen().isEmpty()) {
            existingStaff.setHovaTen(request.getHovaTen());
        }
        if (request.getCccd() != null && !request.getCccd().isEmpty()) {
            existingStaff.setCccd(request.getCccd());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            existingStaff.setPhone(request.getPhone());
        }

        Users updatedStaff = userRepository.save(existingStaff);
        return mapEntityToResponse(updatedStaff);
    }

    @Override
    public void deleteStaff(Integer staffId) {
        // Đã sửa: Dùng hàm đã fix lỗi và check null
        Users staff = userRepository.findByIdAndRole_RoleId(staffId, STAFF_ROLE_ID);
        if (staff == null) {
            throw new RuntimeException("Không tìm thấy Staff với ID: " + staffId);
        }
        userRepository.delete(staff);
    }

    // Phương thức Helper (Hỗ trợ)
    private StaffResponse mapEntityToResponse(Users user) {
        StaffResponse response = new StaffResponse();
        response.setId(user.getId());
        response.setHovaTen(user.getHovaTen());
        response.setEmail(user.getEmail());
        response.setCccd(user.getCccd());
        response.setPhone(user.getPhone());

        if (user.getRole() != null) {
            response.setRoleName(user.getRole().getRoleName()); // Giả sử hàm là getRoleName()
        }
        return response;
    }
}
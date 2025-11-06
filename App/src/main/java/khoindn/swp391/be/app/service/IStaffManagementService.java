// trong /service/IStaffManagementService.java
package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.CreateStaffRequest;
import khoindn.swp391.be.app.model.Request.UpdateStaffRequest;
import khoindn.swp391.be.app.model.Response.StaffResponse;

import java.util.List;

public interface IStaffManagementService {
    StaffResponse createStaff(CreateStaffRequest request);
    StaffResponse getStaffById(Integer staffId);
    List<StaffResponse> getAllStaff();
    StaffResponse updateStaff(Integer staffId, UpdateStaffRequest request);
    void deleteStaff(Integer staffId);
}
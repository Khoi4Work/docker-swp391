// trong /model/Request/UpdateStaffRequest.java
package khoindn.swp391.be.app.model.Request;

import lombok.Data;

@Data
public class UpdateStaffRequest {
    private String hovaTen;
    private String cccd;
    private String phone;
}
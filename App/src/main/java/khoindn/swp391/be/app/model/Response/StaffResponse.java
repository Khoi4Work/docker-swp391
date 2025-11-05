// trong /model/Response/StaffResponse.java
package khoindn.swp391.be.app.model.Response;

import lombok.Data;

@Data
public class StaffResponse {
    private Integer id;
    private String hovaTen;
    private String email;
    private String cccd;
    private String phone;
    private String roleName;
}
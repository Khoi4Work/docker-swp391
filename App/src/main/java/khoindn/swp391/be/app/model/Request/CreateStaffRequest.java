// trong /model/Request/CreateStaffRequest.java
package khoindn.swp391.be.app.model.Request;

import lombok.Data;

@Data
public class CreateStaffRequest {
    private String hovaTen;
    private String email;
    private String password;
    private String cccd;
    private String gplx;
    private String phone;
}
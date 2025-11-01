package khoindn.swp391.be.app.model.formatReq;

import khoindn.swp391.be.app.pojo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVehicleRegisteration {
    int id;
    String hovaten;
    String email;
    String cccd;
    String gplx;
    private UserRole role_id;

}

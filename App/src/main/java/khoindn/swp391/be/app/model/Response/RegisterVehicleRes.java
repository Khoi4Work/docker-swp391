package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.model.formatReq.ResponseVehicleRegisteration;
import khoindn.swp391.be.app.pojo.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVehicleRes {

    private List<ResponseVehicleRegisteration> owners;

    int vehicleId;
    String plateNo;
    String brand;
    String model;
    String color;
    int batteryCapacity;
    private Group group;

}

package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.service.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/vehicle")
@SecurityRequirement(name = "api")
public class VehicleController {

    @Autowired
    private IVehicleService iVehicleService;


    @GetMapping("/getVehicleByGroupID/{groupId}")
    public ResponseEntity<Vehicle> getVehicleByGroupID(@PathVariable int groupId) {
        Vehicle vehicle = iVehicleService.findVehicleByGroupId(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(vehicle);
    }

    @GetMapping("/service")
    public ResponseEntity getMenuVehicleService() {
        return ResponseEntity.status(HttpStatus.OK).body(iVehicleService.getMenuVehicleServices());
    }

    @GetMapping("/service/request")
    public ResponseEntity getVehicleServiceRequest() {

        return  ResponseEntity.status(200).body(iVehicleService.getAllRequestVehicleSerive());

    }

}

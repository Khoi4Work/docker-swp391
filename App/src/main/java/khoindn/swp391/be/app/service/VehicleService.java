package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.NoVehicleInGroupException;
import khoindn.swp391.be.app.exception.exceptions.VehicleIsNotExistedException;
import khoindn.swp391.be.app.pojo.MenuVehicleService;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.repository.IMenuVehicleServiceRepository;
import khoindn.swp391.be.app.repository.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService implements IVehicleService{

    @Autowired
    private IVehicleRepository iVehicleRepository;
    @Autowired
    private IMenuVehicleServiceRepository iMenuVehicleServiceRepository;

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        return iVehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle findVehicleByModel(String name) {
        Vehicle vehicle = iVehicleRepository.findVehicleByModel(name);
        if (vehicle == null) {
            throw new VehicleIsNotExistedException("Vehicle with model '" + name + "' not found");
        }
        return vehicle;
    }

    @Override
    public List<Vehicle> findAll() {
        return iVehicleRepository.findAll();
    }

    @Override
    public Vehicle findVehicleById(int id) {
        Vehicle vehicle = iVehicleRepository.findVehicleByVehicleId(id);
        if (vehicle == null) {
            throw new VehicleIsNotExistedException("Vehicle with id " + id + " not found");
        }
        return vehicle;
    }


    @Override
    public Vehicle findVehicleByGroupId(int groupId) {
        return iVehicleRepository.findVehicleByGroup_GroupId(groupId);
    }

    @Override
    public MenuVehicleService addVehicleService(MenuVehicleService vehicleService) {
        return iMenuVehicleServiceRepository.save(vehicleService);
    }

    @Override
    public List<MenuVehicleService> getAllVehicleServices() {
        return iMenuVehicleServiceRepository.findAll();
    }
}

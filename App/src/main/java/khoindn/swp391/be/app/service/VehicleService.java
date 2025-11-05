package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.GroupMemberNotFoundException;
import khoindn.swp391.be.app.exception.exceptions.NoVehicleInGroupException;
import khoindn.swp391.be.app.exception.exceptions.VehicleIsNotExistedException;
import khoindn.swp391.be.app.pojo.*;
import khoindn.swp391.be.app.repository.IGroupMemberRepository;
import khoindn.swp391.be.app.repository.IMenuVehicleServiceRepository;
import khoindn.swp391.be.app.repository.IRequestVehicleServiceRepository;
import khoindn.swp391.be.app.repository.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService implements IVehicleService {

    @Autowired
    private IVehicleRepository iVehicleRepository;
    @Autowired
    private IMenuVehicleServiceRepository iMenuVehicleServiceRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IGroupMemberRepository iGroupMemberRepository;
    @Autowired
    private IRequestVehicleServiceRepository iRequestVehicleServiceRepository;

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
    public List<MenuVehicleService> getMenuVehicleServices() {
        return iMenuVehicleServiceRepository.findAll();
    }

    @Override
    public RequestVehicleService requestVehicleService(int groupId, int serviceId) {
        Users user = authenticationService.getCurrentAccount();
        GroupMember gm = iGroupMemberRepository.findGroupMembersByUsers_IdAndGroup_GroupId(user.getId(), groupId);
        if (gm == null) {
            throw new GroupMemberNotFoundException("GROUP_NOT_FOUND");
        }
        RequestVehicleService vehicleService = new RequestVehicleService();
        vehicleService.setGroupMember(gm);
        vehicleService.setVehicle(iVehicleRepository.getVehiclesByGroup(gm.getGroup()));
        vehicleService.setRequestVehicleServiceDetail(new RequestVehicleServiceDetail());
        iRequestVehicleServiceRepository.save(vehicleService);

        return vehicleService;
    }

    @Override
    public List<RequestVehicleService> getAllRequestVehicleSerive() {
        return iRequestVehicleServiceRepository.findAll();
    }


}

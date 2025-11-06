package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.MenuVehicleService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMenuVehicleServiceRepository extends JpaRepository<MenuVehicleService, Integer> {
    MenuVehicleService getMenuVehicleServiceByServiceName(String serviceName);

    MenuVehicleService getMenuVehicleServiceByServiceNameContains(String serviceName);
}

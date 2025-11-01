package khoindn.swp391.be.app.config;

import khoindn.swp391.be.app.model.Request.RegisterUserReq;
import khoindn.swp391.be.app.pojo.MenuVehicleService;
import khoindn.swp391.be.app.pojo.UserRole;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.pojo.Vehicle;
import khoindn.swp391.be.app.repository.IMenuVehicleServiceRepository;
import khoindn.swp391.be.app.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IMenuVehicleServiceRepository iMenuVehicleServiceRepository;
    @Autowired
    private IVehicleService iVehicleService;

    @Override
    public void run(String... args) throws Exception {
// test again and again
// test again and again 
        if (!userRoleService.existsByRoleName("user") ||
                !userRoleService.existsByRoleName("co-owner") ||
                !userRoleService.existsByRoleName("admin") ||
                !userRoleService.existsByRoleName("staff")) {

            UserRole userRole = new UserRole();
            userRole.setRoleId(1);
            userRole.setRoleName("user");
            userRoleService.addUserRole(userRole);

            UserRole coOwnerRole = new UserRole();
            coOwnerRole.setRoleId(2);
            coOwnerRole.setRoleName("co-owner");
            userRoleService.addUserRole(coOwnerRole);

            UserRole adminRole = new UserRole();
            adminRole.setRoleId(3);
            adminRole.setRoleName("admin");
            userRoleService.addUserRole(adminRole);

            UserRole staffRole = new UserRole();
            staffRole.setRoleId(4);
            staffRole.setRoleName("staff");
            userRoleService.addUserRole(staffRole);
        }

        if (vehicleService.findAll().isEmpty()) {
            Vehicle v1 = new Vehicle();
            v1.setPlateNo("29A-12345");
            v1.setBrand("Tesla");
            v1.setModel("Model S");
            v1.setColor("Red");
            v1.setBatteryCapacity(100);
            v1.setCreatedAt(LocalDateTime.now());
            v1.setPrice(150000); // thêm giá
            vehicleService.addVehicle(v1);

            Vehicle v2 = new Vehicle();
            v2.setPlateNo("30B-67890");
            v2.setBrand("VinFast");
            v2.setModel("VF8");
            v2.setColor("Blue");
            v2.setBatteryCapacity(90);
            v2.setCreatedAt(LocalDateTime.now());
            v2.setPrice(80000); // thêm giá
            vehicleService.addVehicle(v2);

            Vehicle v3 = new Vehicle();
            v3.setPlateNo("31C-54321");
            v3.setBrand("Nissan");
            v3.setModel("Leaf");
            v3.setColor("White");
            v3.setBatteryCapacity(60);
            v3.setCreatedAt(LocalDateTime.now());
            v3.setPrice(40000); // thêm giá
            vehicleService.addVehicle(v3);

            Vehicle v4 = new Vehicle();
            v4.setPlateNo("32D-11223");
            v4.setBrand("BYD");
            v4.setModel("Han");
            v4.setColor("Black");
            v4.setBatteryCapacity(85);
            v4.setCreatedAt(LocalDateTime.now());
            v4.setPrice(35000); // thêm giá
            vehicleService.addVehicle(v4);

            Vehicle v5 = new Vehicle();
            v5.setPlateNo("33E-44556");
            v5.setBrand("Porsche");
            v5.setModel("Taycan");
            v5.setColor("Silver");
            v5.setBatteryCapacity(93);
            v5.setCreatedAt(LocalDateTime.now());
            v5.setPrice(120000); // thêm giá
            vehicleService.addVehicle(v5);
        }


        if (userService.getAllUsers().isEmpty()) {
            UserRole role = userRoleService.findUserRoleByRoleId(1); // roleId = 1 như JSON của bạn

            Users u1 = new Users();
            u1.setHovaTen("Ndnk");
            u1.setEmail("khoimapu8@gmail.com");
            u1.setPassword("12341234"); // mã hóa mật khẩu
            u1.setCccd("12341234");
            u1.setGplx("12341234");
            u1.setPhone("0918842699");
            u1.setRole(role);

            RegisterUserReq ur1 = modelMapper.map(u1, RegisterUserReq.class);
            authenticationService.register(ur1);

            Users u2 = new Users();
            u2.setHovaTen("NguyenKhoi");
            u2.setEmail("khoimapu2k5@gmail.com");
            u2.setPassword("123123"); // mã hóa mật khẩu
            u2.setCccd("123123123");
            u2.setGplx("123123123");
            u2.setPhone("0966893655");
            u2.setRole(role);

            RegisterUserReq ur2 = modelMapper.map(u2, RegisterUserReq.class);
            authenticationService.register(ur2);

//            Users u3 = new Users();
//            u3.setHovaTen("lamvantuan");
//            u3.setEmail("tlamvantuan@gmail.com");
//            u3.setPassword("123123"); // mã hóa mật khẩu
//            u3.setCccd("123123124");
//            u3.setGplx("123123124");
//            u3.setPhone("0877762076");
//            u3.setRole(role);
//
//            RegisterUserReq ur3 = modelMapper.map(u3, RegisterUserReq.class);
//            authenticationService.register(ur3);
//
//            Users u4 = new Users();
//            u4.setHovaTen("tuan");
//            u4.setEmail("tuanlv.skillcetera@gmail.com");
//            u4.setPassword("123123"); // mã hóa mật khẩu
//            u4.setCccd("123123125");
//            u4.setGplx("123123125");
//            u4.setPhone("0877762075");
//            u4.setRole(role);
//
//            RegisterUserReq ur4 = modelMapper.map(u4, RegisterUserReq.class);
//            authenticationService.register(ur4);
        }

        if (iVehicleService.getAllVehicleServices().isEmpty()) {
            // Car wash service
            MenuVehicleService washService = new MenuVehicleService();
            washService.setServiceName("Car Wash");
            washService.setDescription("Comprehensive car washing service");
            washService.setPrice(500000.0); // example price
            iVehicleService.addVehicleService(washService);

            // Maintenance service
            MenuVehicleService maintenanceService = new MenuVehicleService();
            maintenanceService.setServiceName("Maintenance");
            maintenanceService.setDescription("Periodic car maintenance");
            maintenanceService.setPrice(10000000.0);
            iVehicleService.addVehicleService(maintenanceService);

            // Repair service
            MenuVehicleService repairService = new MenuVehicleService();
            repairService.setServiceName("Repair");
            repairService.setDescription("Minor repair and servicing");
            repairService.setPrice(20000000.0);
            iVehicleService.addVehicleService(repairService);

            // Parts replacement service
            MenuVehicleService replacementService = new MenuVehicleService();
            replacementService.setServiceName("Parts Replacement");
            replacementService.setDescription("Replace genuine car parts");
            replacementService.setPrice(10000000.0);
            iVehicleService.addVehicleService(replacementService);

            // Battery check service (for electric cars)
            MenuVehicleService batteryCheckService = new MenuVehicleService();
            batteryCheckService.setServiceName("Battery Check");
            batteryCheckService.setDescription("Check and optimize battery performance");
            batteryCheckService.setPrice(200000.0);
            iVehicleService.addVehicleService(batteryCheckService);
        }

    }
}

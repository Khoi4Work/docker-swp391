package khoindn.swp391.be.app.controller;



import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.pojo.UserRole;
import khoindn.swp391.be.app.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/UserRole")
@SecurityRequirement(name = "api")
public class UserRoleController {


    @Autowired
    private IUserRoleService iUserRoleService;

    @PostMapping("/")
    public ResponseEntity<UserRole> addUserRole(@RequestBody UserRole userRole) {
        UserRole created = iUserRoleService.addUserRole(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 CREATED
    }

    @GetMapping("/{id}")
    public UserRole getUserRoleById(@PathVariable int id) {
        return iUserRoleService.findUserRoleByRoleId(id);
    }

}

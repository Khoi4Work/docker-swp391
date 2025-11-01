package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:8081")
@SecurityRequirement(name = "api")

public class UserController {

    @Autowired
    private IUserService iUserService;

    // Lấy user theo email (query param)
    @GetMapping("/get")
    public ResponseEntity<Users> getUserByEmail( @RequestParam("email") String email) {
        return ResponseEntity.ok(iUserService.getUserByEmail(email));
    }

    // Tạo mới user
    @PostMapping
    public ResponseEntity<Users> addUser(@RequestBody Users users) {
        Users created = iUserService.addUser(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Cập nhật user theo id
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@RequestBody Users users,
                                            @PathVariable int id) {
        return ResponseEntity.ok(iUserService.updateUser(users, id));
    }

    // Xoá user theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        iUserService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204
    }
}



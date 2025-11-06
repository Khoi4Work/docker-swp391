package khoindn.swp391.be.app.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import khoindn.swp391.be.app.model.Request.LoginUser;
import khoindn.swp391.be.app.model.Request.RegisterUserReq;
import khoindn.swp391.be.app.model.Response.UsersResponse;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:8081")
@SecurityRequirement(name = "api")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@Valid @RequestBody RegisterUserReq users) {
        // send to AuthenticationService
        Users newAccount = authenticationService.register(users);
        System.out.println(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @PostMapping("/login/{roleId}")
    public ResponseEntity<UsersResponse> login(@PathVariable Integer roleId, @RequestBody @Valid LoginUser loginUser) {
        // Kiểm tra roleId với thông tin user
        System.out.println();
        UsersResponse usersResponse = authenticationService.login(loginUser);

        System.out.println(roleId+"-"+usersResponse.getRole().getRoleId());
        if (!roleId.equals(usersResponse.getRole().getRoleId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sai loại tài khoản");
        }

        return ResponseEntity.ok(usersResponse);
    }

    @GetMapping("/current")
    public ResponseEntity<Users> getCurrentAccount(){
        return ResponseEntity.ok(authenticationService.getCurrentAccount());
    }
}

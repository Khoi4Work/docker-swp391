package khoindn.swp391.be.app.model.Request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import khoindn.swp391.be.app.pojo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import javax.management.relation.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserReq {
    @NotBlank(message = "hovaTen cannot blank!!")
    private String hovaTen;  // Changed to match Java naming conventions

    @Email
    @NotBlank(message = "email cannot blank!!")
    private String email;

    @NotBlank(message = "password cannot blank!!")
    private String password;

    @NotBlank(message = "cccd cannot blank!!")
    private String cccd;  // Changed to match Java naming conventions

    @NotBlank(message = "gplx cannot blank!!")
    private String gplx;  // Changed to match Java naming conventions

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(
            regexp = "^(0|\\+84)(3[2-9]|5[2689]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$",
            message = "Phone number must be a valid Vietnamese number"
    )
    private String phone;



    private int  roleId; // Default role ID for regular users
}

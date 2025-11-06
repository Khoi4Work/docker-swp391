package khoindn.swp391.be.app.pojo;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    // attributes
    @Id
    @Column
    private int roleId;
    @Column(unique = true)
    private String roleName;

    // relationship
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Users> users;
}

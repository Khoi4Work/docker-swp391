package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuVehicleService {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", unique = true)
    private String serviceName;
    @Column(name = "description", unique = true)
    private String description;
    @Column(name = "price")
    private Double price;
    // Relationships
    @OneToMany(mappedBy = "menuVehicleService", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RequestVehicleService> requestVehicleServices;
}

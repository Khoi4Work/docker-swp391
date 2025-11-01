package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "vehicles")
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private int vehicleId;

    @Column(name = "plate_no", length = 32, nullable = false)
    private String plateNo;

    @Column(name = "brand", length = 32)
    private String brand;

    @Column(name = "model", length = 32)
    private String model;

    @Column(name = "color", length = 32)
    private String color;

    @Column(name = "battery_capacity")
    private int batteryCapacity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name="price", length = 32)
    private int price;

    @Column(name="image",length=32)
    private String imageUrl;

    // Relationships

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    private Group group;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RequestVehicleService> requestVehicleServices = new ArrayList<>();


}
